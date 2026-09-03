package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_articles")
data class NewsArticle(
    @PrimaryKey
    val id: String,
    val title: String,
    val summary: String,
    val fullContent: String,
    val source: String,
    val url: String,
    val imageUrl: String = "",
    val publishedAt: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String, // "PALESTINE", "WORLD", "HUMANITARIAN", "DIPLOMACY", "ANALYSIS", "BREAKING"
    val isPalestine: Boolean = false,
    val isBreaking: Boolean = false,
    val isLive: Boolean = false,
    val isBookmarked: Boolean = false,
    val readCount: Int = 0,
    val location: String = "GLOBAL",
    val keyTakeaways: String = "", // Comma or bullet separated
    val unReportReference: String = "" // Optional UN/Official document or resolution ref
) {
    val estimatedReadTimeMin: Int
        get() = maxOf(1, kotlin.math.ceil(fullContent.split(Regex("\\s+")).size / 200.0).toInt())
}
