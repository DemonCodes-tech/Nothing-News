package com.example.util

import com.example.BuildConfig
import com.example.data.model.ArticleMemory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiMemoryExtractor {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun extractMemory(articleId: String, articleText: String): ArticleMemory? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") return@withContext null

        val prompt = "Extract 5 key facts, 2 controversial claims, and 3 relevant entities (people/orgs) from this article. Provide them strictly according to the JSON schema.\n\nArticle: $articleText"
        
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
                put("temperature", 0.3)
                put("responseMimeType", "application/json")
                put("responseSchema", JSONObject().apply {
                    put("type", "OBJECT")
                    put("properties", JSONObject().apply {
                        put("facts", JSONObject().apply {
                            put("type", "ARRAY")
                            put("items", JSONObject().apply { put("type", "STRING") })
                        })
                        put("claims", JSONObject().apply {
                            put("type", "ARRAY")
                            put("items", JSONObject().apply { put("type", "STRING") })
                        })
                        put("entities", JSONObject().apply {
                            put("type", "ARRAY")
                            put("items", JSONObject().apply { put("type", "STRING") })
                        })
                    })
                    put("required", JSONArray().apply {
                        put("facts")
                        put("claims")
                        put("entities")
                    })
                })
            })
        }

        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            // The prompt requested Gemini 1.5 Pro, let's use gemini-1.5-pro or gemini-1.5-pro-latest
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-latest:generateContent?key=$apiKey")
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
                
                return@withContext ArticleMemory(
                    articleId = articleId,
                    factsJson = resultObj.optJSONArray("facts")?.toString() ?: "[]",
                    claimsJson = resultObj.optJSONArray("claims")?.toString() ?: "[]",
                    entitiesJson = resultObj.optJSONArray("entities")?.toString() ?: "[]"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}
