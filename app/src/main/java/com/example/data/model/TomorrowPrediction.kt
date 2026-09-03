package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tomorrow_predictions")
data class TomorrowPrediction(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val predictedHeadline: String,
    val timeframe: String = "NEXT 12 HOURS",
    val category: String = "GLOBAL",
    val confidenceScore: Int = 92,
    val backgrounder: String, // 100-word backgrounder explaining the context needed to understand tomorrow's news
    val triggerSignalsJson: String = "[]", // Key signals/events from today's top stories
    val timestamp: Long = System.currentTimeMillis()
)
