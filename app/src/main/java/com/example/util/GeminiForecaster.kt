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

object GeminiForecaster {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    data class ForecastScenario(val type: String, val title: String, val description: String)

    suspend fun generateScenarios(articleText: String): List<ForecastScenario>? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") return@withContext null

        val prompt = "Analyze the following news article and generate three possible future scenarios based on these events: Best Case, Worst Case, and Most Likely. Explain what they would mean for the global economy, stock market, or specific industry involved.\n\nArticle: $articleText"
        
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
                put("temperature", 0.7)
                put("responseMimeType", "application/json")
                put("responseSchema", JSONObject().apply {
                    put("type", "ARRAY")
                    put("items", JSONObject().apply {
                        put("type", "OBJECT")
                        put("properties", JSONObject().apply {
                            put("type", JSONObject().apply {
                                put("type", "STRING")
                                put("description", "e.g., 'BEST CASE', 'WORST CASE', 'MOST LIKELY'")
                            })
                            put("title", JSONObject().apply {
                                put("type", "STRING")
                                put("description", "A punchy 3-5 word title for the scenario")
                            })
                            put("description", JSONObject().apply {
                                put("type", "STRING")
                                put("description", "A 2-3 sentence explanation of the scenario and its market/industry impact")
                            })
                        })
                        put("required", JSONArray().apply {
                            put("type")
                            put("title")
                            put("description")
                        })
                    })
                })
            })
        }

        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val responseBody = response.body?.string() ?: return@withContext null
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates") ?: return@withContext null
                val firstCandidate = candidates.optJSONObject(0) ?: return@withContext null
                val content = firstCandidate.optJSONObject("content") ?: return@withContext null
                val parts = content.optJSONArray("parts") ?: return@withContext null
                val text = parts.optJSONObject(0)?.optString("text") ?: return@withContext null

                val scenariosArray = JSONArray(text)
                val results = mutableListOf<ForecastScenario>()
                for (i in 0 until scenariosArray.length()) {
                    val obj = scenariosArray.getJSONObject(i)
                    results.add(ForecastScenario(
                        type = obj.getString("type"),
                        title = obj.getString("title"),
                        description = obj.getString("description")
                    ))
                }
                return@withContext results
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}
