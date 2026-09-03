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

object GeminiFactChecker {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    data class FactCheckResult(
        val claims: List<String>,
        val biases: List<String>,
        val verdict: String
    )

    suspend fun analyzeArticle(articleText: String): FactCheckResult? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") return@withContext null

        val prompt = "Analyze this article for factual integrity and bias. 1. Extract 2-3 core factual claims. 2. Identify any editorial biases, loaded language, or missing context. 3. Provide a brief 1-sentence overall verdict on its neutrality.\n\nArticle: $articleText"
        
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
                        put("claims", JSONObject().apply {
                            put("type", "ARRAY")
                            put("items", JSONObject().apply { put("type", "STRING") })
                        })
                        put("biases", JSONObject().apply {
                            put("type", "ARRAY")
                            put("items", JSONObject().apply { put("type", "STRING") })
                        })
                        put("verdict", JSONObject().apply {
                            put("type", "STRING")
                        })
                    })
                    put("required", JSONArray().apply {
                        put("claims")
                        put("biases")
                        put("verdict")
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
                val text = candidates.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text") ?: return@withContext null

                val resultObj = JSONObject(text)
                
                val claimsArray = resultObj.optJSONArray("claims")
                val claimsList = mutableListOf<String>()
                if (claimsArray != null) {
                    for (i in 0 until claimsArray.length()) {
                        claimsList.add(claimsArray.getString(i))
                    }
                }
                
                val biasesArray = resultObj.optJSONArray("biases")
                val biasesList = mutableListOf<String>()
                if (biasesArray != null) {
                    for (i in 0 until biasesArray.length()) {
                        biasesList.add(biasesArray.getString(i))
                    }
                }
                
                return@withContext FactCheckResult(
                    claims = claimsList,
                    biases = biasesList,
                    verdict = resultObj.optString("verdict", "Analysis complete.")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}
