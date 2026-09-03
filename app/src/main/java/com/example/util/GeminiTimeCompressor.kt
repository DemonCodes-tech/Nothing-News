package com.example.util

import com.example.BuildConfig
import com.example.data.model.NewsArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object GeminiTimeCompressor {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // In-memory cache to prevent duplicate calls when scrubbing back and forth
    private val memoryCache = ConcurrentHashMap<String, String>()

    /**
     * Instantly generates an ultra-short variant locally so the UI renders with zero lag.
     */
    fun getInstantLocalSummary(article: NewsArticle, minutes: Int): String {
        val cacheKey = "${article.id}_$minutes"
        memoryCache[cacheKey]?.let { return it }

        val targetWords = minutes * 15
        val fallback = when (minutes) {
            1 -> {
                // Single bolded headline + one punchy sentence
                val punchySentence = article.summary
                    .split(Regex("[.!?]"))
                    .map { it.trim() }
                    .firstOrNull { it.isNotEmpty() } ?: article.summary
                "**${article.title}** — $punchySentence."
            }
            5 -> {
                // ~75 words: summary + top takeaway
                val words = "${article.summary} Key takeaway: ${article.keyTakeaways.take(150)}"
                    .split(Regex("\\s+"))
                    .take(targetWords)
                    .joinToString(" ")
                "$words..."
            }
            15 -> {
                // ~225 words: comprehensive summary
                val words = "${article.summary} ${article.keyTakeaways} ${article.fullContent}"
                    .split(Regex("\\s+"))
                    .take(targetWords)
                    .joinToString(" ")
                "$words..."
            }
            else -> {
                // 30 min mode: full deep dive with backgrounders
                "**Deep-Dive Analysis & Background:**\n\n${article.fullContent}\n\n• Strategic Context: ${article.keyTakeaways}"
            }
        }
        return fallback
    }

    /**
     * Batch processes articles using Gemini 1.5 Flash (via gemini-3.5-flash endpoint).
     * Prompt: 'Summarize this article in [X] words, where X = (selected_minutes * 15). Prioritize the most actionable insight first.'
     */
    suspend fun compressArticlesBatch(articles: List<NewsArticle>, minutes: Int): Map<String, String> = withContext(Dispatchers.IO) {
        val targetWords = minutes * 15
        val resultMap = mutableMapOf<String, String>()

        // 1. Check which articles are missing from cache
        val articlesToProcess = articles.take(10).filter { article ->
            val cacheKey = "${article.id}_$minutes"
            val cached = memoryCache[cacheKey]
            if (cached != null) {
                resultMap[article.id] = cached
                false
            } else {
                true
            }
        }

        if (articlesToProcess.isEmpty()) {
            return@withContext resultMap
        }

        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Provide instant local algorithmic variant if no API key
            articlesToProcess.forEach { article ->
                val local = getInstantLocalSummary(article, minutes)
                resultMap[article.id] = local
                memoryCache["${article.id}_$minutes"] = local
            }
            return@withContext resultMap
        }

        // Build batch prompt
        val promptBuilder = StringBuilder()
        promptBuilder.append("You are a real-time news compression intelligence engine.\n")
        promptBuilder.append("For each article below, summarize it in exactly [X] words, where X = $targetWords (based on $minutes minutes before the user's next meeting). Prioritize the most actionable insight first.\n")
        if (minutes == 1) {
            promptBuilder.append("CRITICAL: In '1-min mode', you MUST output ONLY a single bolded headline followed by one punchy, high-impact sentence (approx 15 words total). Nothing else.\n")
        } else if (minutes == 30) {
            promptBuilder.append("In '30-min mode', provide the full deep-dive with backgrounders and structural context (approx 450 words).\n")
        } else {
            promptBuilder.append("In '$minutes-min mode', provide a punchy $targetWords-word briefing highlighting key causal impacts.\n")
        }
        promptBuilder.append("Return ONLY a JSON object mapping each article ID to its compressed summary string:\n")
        promptBuilder.append("{\n  \"<article_id>\": \"<compressed_summary>\", ...\n}\n\n")

        promptBuilder.append("Articles to process:\n")
        articlesToProcess.forEach { art ->
            promptBuilder.append("--- ARTICLE START ---\n")
            promptBuilder.append("ID: ${art.id}\n")
            promptBuilder.append("TITLE: ${art.title}\n")
            promptBuilder.append("TEXT: ${art.summary} ${art.fullContent.take(500)}\n")
            promptBuilder.append("--- ARTICLE END ---\n\n")
        }

        val jsonRequest = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", promptBuilder.toString())
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.3)
                put("responseMimeType", "application/json")
            })
        }

        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val json = JSONObject(responseBody)
                        val text = json.optJSONArray("candidates")
                            ?.optJSONObject(0)
                            ?.optJSONObject("content")
                            ?.optJSONArray("parts")
                            ?.optJSONObject(0)
                            ?.optString("text")

                        if (!text.isNullOrEmpty()) {
                            val parsed = JSONObject(text)
                            val keys = parsed.keys()
                            while (keys.hasNext()) {
                                val id = keys.next()
                                val summary = parsed.optString(id)
                                if (summary.isNotBlank()) {
                                    resultMap[id] = summary
                                    memoryCache["${id}_$minutes"] = summary
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Fill any missed articles with local fallback
        articlesToProcess.forEach { article ->
            if (!resultMap.containsKey(article.id)) {
                val fallback = getInstantLocalSummary(article, minutes)
                resultMap[article.id] = fallback
                memoryCache["${article.id}_$minutes"] = fallback
            }
        }

        return@withContext resultMap
    }
}
