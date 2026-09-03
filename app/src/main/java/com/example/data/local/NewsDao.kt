package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.NewsArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news_articles ORDER BY timestamp DESC")
    fun getAllArticles(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE isPalestine = 1 ORDER BY timestamp DESC")
    fun getPalestineArticles(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE isPalestine = 0 ORDER BY timestamp DESC")
    fun getWorldArticles(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedArticles(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE isLive = 1 OR isBreaking = 1 ORDER BY timestamp DESC")
    fun getLiveAndBreakingArticles(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE category = :category ORDER BY timestamp DESC")
    fun getArticlesByCategory(category: String): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE id = :id")
    suspend fun getArticleById(id: String): NewsArticle?

    @Query("""
        SELECT * FROM news_articles 
        WHERE title LIKE '%' || :query || '%' 
           OR summary LIKE '%' || :query || '%' 
           OR fullContent LIKE '%' || :query || '%' 
           OR location LIKE '%' || :query || '%'
           OR source LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    fun searchArticles(query: String): Flow<List<NewsArticle>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticles(articles: List<NewsArticle>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: NewsArticle)

    @Update
    suspend fun updateArticle(article: NewsArticle)

    @Query("UPDATE news_articles SET isBookmarked = :bookmarked WHERE id = :id")
    suspend fun setBookmark(id: String, bookmarked: Boolean)

    @Query("UPDATE news_articles SET readCount = readCount + 1 WHERE id = :id")
    suspend fun incrementReadCount(id: String)

    @Query("DELETE FROM news_articles WHERE id = :id")
    suspend fun deleteArticleById(id: String)

    @Query("DELETE FROM news_articles WHERE isBookmarked = 0 AND timestamp < :timestampThreshold")
    suspend fun deleteOldUnbookmarkedArticles(timestampThreshold: Long)

    @Query("SELECT COUNT(*) FROM news_articles")
    suspend fun getArticleCount(): Int
    @Query("SELECT * FROM article_memories WHERE articleId = :articleId")
    suspend fun getMemoryByArticleId(articleId: String): com.example.data.model.ArticleMemory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticleMemory(memory: com.example.data.model.ArticleMemory)

    @Query("SELECT * FROM article_memories ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMemories(limit: Int = 20): List<com.example.data.model.ArticleMemory>

    @Query("SELECT * FROM tomorrow_predictions ORDER BY timestamp DESC")
    fun getTomorrowPredictionsFlow(): Flow<List<com.example.data.model.TomorrowPrediction>>

    @Query("SELECT * FROM tomorrow_predictions ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getLatestTomorrowPredictions(limit: Int = 5): List<com.example.data.model.TomorrowPrediction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTomorrowPredictions(predictions: List<com.example.data.model.TomorrowPrediction>)

    @Query("DELETE FROM tomorrow_predictions")
    suspend fun clearTomorrowPredictions()
}
