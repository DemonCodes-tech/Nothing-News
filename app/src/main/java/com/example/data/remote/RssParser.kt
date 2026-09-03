package com.example.data.remote

import com.example.data.model.NewsArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.util.UUID
import java.util.concurrent.TimeUnit

class RssParser {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun fetchFeed(url: String, sourceName: String, defaultCategory: String, isPalestineFeed: Boolean): List<NewsArticle> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "NothingNews/1.0 (Android Monochrome Feed)")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()

            val xmlContent = response.body?.string() ?: return@withContext emptyList()
            parseXml(xmlContent, sourceName, defaultCategory, isPalestineFeed)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parseXml(xml: String, sourceName: String, defaultCategory: String, isPalestineFeed: Boolean): List<NewsArticle> {
        val articles = mutableListOf<NewsArticle>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            var eventType = parser.eventType
            var insideItem = false
            var title = ""
            var description = ""
            var link = ""
            var pubDate = ""
            var imageUrl = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (tagName.equals("item", ignoreCase = true) || tagName.equals("entry", ignoreCase = true)) {
                            insideItem = true
                            title = ""
                            description = ""
                            link = ""
                            pubDate = ""
                            imageUrl = ""
                        } else if (insideItem) {
                            when (tagName.lowercase()) {
                                "title" -> title = cleanHtml(parser.nextText())
                                "description", "summary", "content" -> description = cleanHtml(parser.nextText())
                                "link" -> {
                                    val text = parser.nextText()
                                    link = if (text.isNotBlank()) text else parser.getAttributeValue(null, "href") ?: ""
                                }
                                "pubdate", "published", "updated" -> pubDate = parser.nextText()
                                "enclosure", "media:content" -> {
                                    val url = parser.getAttributeValue(null, "url")
                                    val type = parser.getAttributeValue(null, "type") ?: ""
                                    if (url != null && type.startsWith("image/")) {
                                        imageUrl = url
                                    } else if (url != null && imageUrl.isBlank()) {
                                        imageUrl = url // Fallback if no type but url exists
                                    }
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (tagName.equals("item", ignoreCase = true) || tagName.equals("entry", ignoreCase = true)) {
                            if (title.isNotBlank() && (description.isNotBlank() || link.isNotBlank())) {
                                // Extract image from description if still blank
                                if (imageUrl.isBlank()) {
                                    val imgRegex = Regex("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>")
                                    val matchResult = imgRegex.find(description)
                                    if (matchResult != null) {
                                        imageUrl = matchResult.groupValues[1]
                                    }
                                }
                                
                                val isPal = isPalestineFeed || 
                                    title.contains("Palestine", ignoreCase = true) || 
                                    title.contains("Gaza", ignoreCase = true) || 
                                    title.contains("West Bank", ignoreCase = true) || 
                                    title.contains("Rafah", ignoreCase = true) ||
                                    title.contains("Jerusalem", ignoreCase = true) ||
                                    description.contains("Palestine", ignoreCase = true) ||
                                    description.contains("Gaza", ignoreCase = true)

                                val category = when {
                                    isPal && (description.contains("aid", ignoreCase = true) || description.contains("hospital", ignoreCase = true) || description.contains("UN", ignoreCase = true)) -> "HUMANITARIAN"
                                    isPal -> "PALESTINE"
                                    title.contains("UN", ignoreCase = true) || title.contains("Court", ignoreCase = true) || title.contains("Summit", ignoreCase = true) -> "DIPLOMACY"
                                    else -> defaultCategory
                                }

                                val id = "rss_" + UUID.nameUUIDFromBytes(title.toByteArray()).toString().substring(0, 10)
                                articles.add(
                                    NewsArticle(
                                        id = id,
                                        title = title.trim(),
                                        summary = if (description.length > 250) cleanHtml(description).take(247) + "..." else cleanHtml(description),
                                        fullContent = cleanHtml(description).ifBlank { title },
                                        source = sourceName,
                                        url = link,
                                        imageUrl = imageUrl,
                                        publishedAt = formatPubDate(pubDate),
                                        timestamp = System.currentTimeMillis() - (articles.size * 60000L),
                                        category = category,
                                        isPalestine = isPal,
                                        isBreaking = title.contains("BREAKING", ignoreCase = true) || title.contains("URGENT", ignoreCase = true),
                                        isLive = true,
                                        location = if (isPal) "PALESTINE // REGIONAL" else "GLOBAL DISPATCH",
                                        keyTakeaways = generateKeyTakeaways(description, title)
                                    )
                                )
                            }
                            insideItem = false
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return articles
    }

    private fun cleanHtml(html: String): String {
        return html.replace(Regex("<.*?>"), "")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
            .replace("&#39;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&nbsp;", " ")
            .trim()
    }

    private fun formatPubDate(dateStr: String): String {
        if (dateStr.isBlank()) return "LIVE // TODAY"
        return try {
            if (dateStr.length > 22) dateStr.substring(0, 22) else dateStr
        } catch (e: Exception) {
            "LIVE // RECENT"
        }
    }

    private fun generateKeyTakeaways(description: String, title: String): String {
        val sentences = description.split(Regex("[.!?]")).filter { it.isNotBlank() }
        return if (sentences.isNotEmpty()) {
            sentences.take(2).joinToString(" • ") { it.trim() }
        } else {
            title
        }
    }
}
