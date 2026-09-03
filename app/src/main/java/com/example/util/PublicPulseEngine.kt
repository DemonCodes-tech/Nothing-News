package com.example.util

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.net.URLEncoder

object PublicPulseEngine {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    data class PulseResult(
        val overallSentiment: String,
        val sentimentScore: Int,
        val confidence: Double,
        val topReason: String,
        val pulseSummary: String,
        val platformBreakdown: Map<String, Int>,
        val keyThemes: List<String>,
        val representativeComments: List<CommentRep>
    )

    data class CommentRep(
        val text: String,
        val platform: String,
        val author: String,
        val sentiment: String // "Positive" | "Negative" | "Neutral"
    )

    data class SocialComment(
        val text: String,
        val platform: String,
        val author: String,
        val upvotes: Int
    )

    private val cache = mutableMapOf<String, Pair<Long, PulseResult>>()
    private val CACHE_DURATION_MS = 6 * 60 * 60 * 1000L // 6 hours

    suspend fun analyzeArticle(headline: String, url: String): PulseResult? = withContext(Dispatchers.IO) {
        val cacheKey = url.ifBlank { headline }
        val cached = cache[cacheKey]
        if (cached != null && System.currentTimeMillis() - cached.first < CACHE_DURATION_MS) {
            return@withContext cached.second
        }

        val allComments = mutableListOf<SocialComment>()

        // 1. Fetch Reddit Comments
        try {
            val redditQuery = URLEncoder.encode("$headline OR $url", "UTF-8")
            val redditUrl = "https://www.reddit.com/search.json?q=\$redditQuery&sort=top&t=week&limit=10"
            val redditReq = Request.Builder()
                .url(redditUrl)
                .addHeader("User-Agent", "NothingNews/1.0")
                .get()
                .build()

            client.newCall(redditReq).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (body != null) {
                        val json = JSONObject(body)
                        val children = json.optJSONObject("data")?.optJSONArray("children")
                        if (children != null) {
                            for (i in 0 until Math.min(children.length(), 10)) {
                                val postData = children.optJSONObject(i)?.optJSONObject("data")
                                if (postData != null) {
                                    val title = postData.optString("title", "")
                                    val text = postData.optString("selftext", "")
                                    val author = postData.optString("author", "unknown")
                                    val ups = postData.optInt("ups", 0)
                                    val combinedText = if (text.isNotBlank()) "\$title - \$text" else title
                                    allComments.add(SocialComment(combinedText.take(500), "Reddit", author, ups))
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Fetch Twitter Comments
        try {
            val twitterKey = try { BuildConfig::class.java.getField("TWITTER_API_KEY").get(null) as String } catch(e:Exception) { "" }
            if (twitterKey.isNotBlank() && twitterKey != "MY_TWITTER_API_KEY") {
                val cleanHeadline = headline.split(" ").filter { it.length > 3 }.joinToString(" ")
                val twitterQuery = URLEncoder.encode("(\$cleanHeadline) OR \$url -is:retweet", "UTF-8")
                val twitterUrl = "https://api.twitter.com/2/tweets/search/recent?query=\$twitterQuery&max_results=15&tweet.fields=public_metrics,author_id&expansions=author_id"
                val twitterReq = Request.Builder()
                    .url(twitterUrl)
                    .addHeader("Authorization", "Bearer \$twitterKey")
                    .get()
                    .build()

                client.newCall(twitterReq).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        if (body != null) {
                            val json = JSONObject(body)
                            val data = json.optJSONArray("data")
                            val includes = json.optJSONObject("includes")?.optJSONArray("users")
                            val userMap = mutableMapOf<String, String>()
                            if (includes != null) {
                                for (i in 0 until includes.length()) {
                                    val u = includes.optJSONObject(i)
                                    if (u != null) {
                                        userMap[u.optString("id")] = u.optString("username", "unknown")
                                    }
                                }
                            }

                            if (data != null) {
                                for (i in 0 until data.length()) {
                                    val tweet = data.optJSONObject(i)
                                    if (tweet != null) {
                                        val text = tweet.optString("text", "")
                                        val authorId = tweet.optString("author_id", "")
                                        val author = userMap[authorId] ?: authorId
                                        val metrics = tweet.optJSONObject("public_metrics")
                                        val likes = metrics?.optInt("like_count", 0) ?: 0
                                        allComments.add(SocialComment(text.take(500), "X", author, likes))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (allComments.isEmpty()) {
            return@withContext null
        }

        // 3. Aggregate with Gemini
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") return@withContext null

        val commentsPayload = allComments.joinToString("\n") { "[\${it.platform} | @\${it.author}]: \${it.text}" }

        val prompt = """
            You are a social sentiment analyst. I have attached a list of comments from X (Twitter) and Reddit discussing a specific news story. Analyze the entire collection and return ONLY a JSON object with:
            overall_sentiment: One of ['overwhelmingly positive', 'mostly positive', 'mixed/neutral', 'mostly negative', 'overwhelmingly negative']
            sentiment_score: A numerical score from -100 (extremely negative) to +100 (extremely positive)
            confidence: A number between 0.0 and 1.0 representing how confident you are in the score (based on comment volume and clarity)
            top_reason: A single sentence summarizing why the crowd feels this way
            pulse_summary: A short, punchy 10-word summary capturing the vibe
            platform_breakdown: Separate scores for 'X' and 'Reddit' (e.g., {"X": -45, "Reddit": 20})
            key_themes: Up to 3 recurring topics mentioned in the comments
            most_representative_comments: Extract the 3 comments (mix from both platforms) that best represent the overall sentiment — include the text, platform, and author. And add a sentiment field ("Positive", "Negative", or "Neutral").
            
            Comments:
            $commentsPayload
        """.trimIndent()

        val jsonRequest = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.4)
                put("responseMimeType", "application/json")
                put("responseSchema", JSONObject().apply {
                    put("type", "OBJECT")
                    put("properties", JSONObject().apply {
                        put("overall_sentiment", JSONObject().apply { put("type", "STRING") })
                        put("sentiment_score", JSONObject().apply { put("type", "INTEGER") })
                        put("confidence", JSONObject().apply { put("type", "NUMBER") })
                        put("top_reason", JSONObject().apply { put("type", "STRING") })
                        put("pulse_summary", JSONObject().apply { put("type", "STRING") })
                        put("platform_breakdown", JSONObject().apply {
                            put("type", "OBJECT")
                            put("properties", JSONObject().apply {
                                put("X", JSONObject().apply { put("type", "INTEGER") })
                                put("Reddit", JSONObject().apply { put("type", "INTEGER") })
                            })
                        })
                        put("key_themes", JSONObject().apply {
                            put("type", "ARRAY")
                            put("items", JSONObject().apply { put("type", "STRING") })
                        })
                        put("most_representative_comments", JSONObject().apply {
                            put("type", "ARRAY")
                            put("items", JSONObject().apply {
                                put("type", "OBJECT")
                                put("properties", JSONObject().apply {
                                    put("text", JSONObject().apply { put("type", "STRING") })
                                    put("platform", JSONObject().apply { put("type", "STRING") })
                                    put("author", JSONObject().apply { put("type", "STRING") })
                                    put("sentiment", JSONObject().apply { put("type", "STRING") })
                                })
                            })
                        })
                    })
                })
            })
        }

        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val responseBody = response.body?.string() ?: return@withContext null
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates") ?: return@withContext null
                val text = candidates.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text") ?: return@withContext null
                val resultObj = JSONObject(text)

                val pbObj = resultObj.optJSONObject("platform_breakdown")
                val pb = mapOf(
                    "X" to (pbObj?.optInt("X", 0) ?: 0),
                    "Reddit" to (pbObj?.optInt("Reddit", 0) ?: 0)
                )

                val ktArray = resultObj.optJSONArray("key_themes")
                val kt = mutableListOf<String>()
                if (ktArray != null) {
                    for (i in 0 until ktArray.length()) {
                        kt.add(ktArray.getString(i))
                    }
                }

                val mrcArray = resultObj.optJSONArray("most_representative_comments")
                val mrc = mutableListOf<CommentRep>()
                if (mrcArray != null) {
                    for (i in 0 until mrcArray.length()) {
                        val c = mrcArray.getJSONObject(i)
                        mrc.add(
                            CommentRep(
                                text = c.optString("text", ""),
                                platform = c.optString("platform", ""),
                                author = c.optString("author", ""),
                                sentiment = c.optString("sentiment", "Neutral")
                            )
                        )
                    }
                }

                val res = PulseResult(
                    overallSentiment = resultObj.optString("overall_sentiment", "mixed/neutral"),
                    sentimentScore = resultObj.optInt("sentiment_score", 0),
                    confidence = resultObj.optDouble("confidence", 0.5),
                    topReason = resultObj.optString("top_reason", ""),
                    pulseSummary = resultObj.optString("pulse_summary", ""),
                    platformBreakdown = pb,
                    keyThemes = kt,
                    representativeComments = mrc
                )
                
                cache[cacheKey] = Pair(System.currentTimeMillis(), res)
                return@withContext res
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}
