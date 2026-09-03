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

object GeminiAlertGenerator {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM_PROMPT = """System Prompt: Breaking News Push Notification Generator
Role & Goal:
You are an executive news desk editor specializing in breaking news push notifications. Your goal is to write authoritative, high-priority mobile alerts that deliver essential facts instantly while preserving absolute accuracy.
Core Rules:
Character Limits: Title: Max 35 characters. Body: Max 85 characters (ensures full visibility on locked screens).
Speed & Directness: Lead with what happened immediately. Use strong action verbs in the present or past tense.
Zero Speculation: Stick strictly to confirmed facts. Never exaggerate, use clickbait, or speculate on unverified details.
Tone: High urgency, neutral, objective, and clear. Avoid exclamation marks (!).
Format: Provide 3 distinct variants based on alert urgency levels.
Output Structure:
Variant 1: Direct Alert (Standard Breaking)
Title: [PUNCHY EVENT / LOCATION]
Body: [Core outcome or immediate facts]
Variant 2: Developing Story (Ongoing Event)
Title: [EVENT + "DEVELOPING"]
Body: [What is happening right now + safety/action note if applicable]
Variant 3: High Impact (Major Global/National Event)
Title: [URGENT: MAJOR EVENT]
Body: [The single most important consequence or quote]"""

    data class GeneratedAlert(val title: String, val body: String)

    suspend fun generateAlert(headline: String, summary: String): GeneratedAlert? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") return@withContext null

        val prompt = "Headline: ${headline}\nSummary: ${summary}\nGenerate the 3 variants according to the system prompt."
        
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
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", SYSTEM_PROMPT)
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.4)
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

                // Try to parse Variant 3, fallback to Variant 1
                return@withContext parseVariant(text, "Variant 3") ?: parseVariant(text, "Variant 1")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    private fun parseVariant(text: String, variantPrefix: String): GeneratedAlert? {
        try {
            val lines = text.lines().map { it.trim() }
            var inVariant = false
            var title: String? = null
            var body: String? = null
            
            for (line in lines) {
                if (line.startsWith(variantPrefix)) {
                    inVariant = true
                    continue
                }
                if (inVariant) {
                    if (line.startsWith("Title:")) {
                        title = line.removePrefix("Title:").trim().removeSurrounding("**").trim()
                    } else if (line.startsWith("Body:")) {
                        body = line.removePrefix("Body:").trim().removeSurrounding("**").trim()
                        if (title != null) {
                            return GeneratedAlert(title, body)
                        }
                    } else if (line.startsWith("Variant")) {
                        // Moved to next variant
                        break
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        return null
    }
}
