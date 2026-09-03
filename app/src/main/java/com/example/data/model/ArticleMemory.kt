package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_memories")
data class ArticleMemory(
    @PrimaryKey
    val articleId: String,
    val factsJson: String, // List of key facts as JSON array string
    val claimsJson: String, // List of controversial claims as JSON array string
    val entitiesJson: String, // List of relevant entities as JSON array string
    val timestamp: Long = System.currentTimeMillis()
)
