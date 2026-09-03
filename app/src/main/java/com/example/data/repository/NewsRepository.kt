package com.example.data.repository

import com.example.data.local.NewsDao
import com.example.data.model.NewsArticle
import com.example.data.remote.RssParser
import com.example.data.sample.CuratedDispatches
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NewsRepository(
    private val newsDao: NewsDao,
    private val rssParser: RssParser = RssParser()
) {
    val allArticles: Flow<List<NewsArticle>> = newsDao.getAllArticles()
    val palestineArticles: Flow<List<NewsArticle>> = newsDao.getPalestineArticles()
    val worldArticles: Flow<List<NewsArticle>> = newsDao.getWorldArticles()
    val bookmarkedArticles: Flow<List<NewsArticle>> = newsDao.getBookmarkedArticles()
    val liveAndBreaking: Flow<List<NewsArticle>> = newsDao.getLiveAndBreakingArticles()

    fun search(query: String): Flow<List<NewsArticle>> = newsDao.searchArticles(query)
    fun getByCategory(category: String): Flow<List<NewsArticle>> = newsDao.getArticlesByCategory(category)

    suspend fun initializeDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        val count = newsDao.getArticleCount()
        if (count == 0) {
            val curated = CuratedDispatches.getInitialArticles()
            newsDao.insertArticles(curated)
        }
    }

    suspend fun syncLiveFeeds(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val fetchedArticles = mutableListOf<NewsArticle>()

            // 1. Al Jazeera English RSS
            val ajArticles = rssParser.fetchFeed(
                url = "https://www.aljazeera.com/xml/rss/all.xml",
                sourceName = "AL JAZEERA",
                defaultCategory = "PALESTINE",
                isPalestineFeed = false
            )
            fetchedArticles.addAll(ajArticles)

            // 2. UN News Top Stories
            val unArticles = rssParser.fetchFeed(
                url = "https://news.un.org/feed/subscribe/en/news/all/rss.xml",
                sourceName = "UN NEWS",
                defaultCategory = "WORLD",
                isPalestineFeed = false
            )
            fetchedArticles.addAll(unArticles)

            // 3. BBC World RSS
            val bbcArticles = rssParser.fetchFeed(
                url = "https://feeds.bbci.co.uk/news/world/rss.xml",
                sourceName = "BBC WORLD",
                defaultCategory = "WORLD",
                isPalestineFeed = false
            )
            fetchedArticles.addAll(bbcArticles)

            if (fetchedArticles.isNotEmpty()) {
                newsDao.insertArticles(fetchedArticles)
                // Cache pruning: delete unbookmarked articles older than 7 days
                val sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
                newsDao.deleteOldUnbookmarkedArticles(sevenDaysAgo)
                Result.success(fetchedArticles.size)
            } else {
                // Ensure curated articles exist
                initializeDatabaseIfEmpty()
                Result.success(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback ensure database has initial curated articles
            initializeDatabaseIfEmpty()
            Result.failure(e)
        }
    }

    suspend fun toggleBookmark(articleId: String, currentBookmarked: Boolean) = withContext(Dispatchers.IO) {
        newsDao.setBookmark(articleId, !currentBookmarked)
    }

    suspend fun recordArticleRead(articleId: String) = withContext(Dispatchers.IO) {
        newsDao.incrementReadCount(articleId)
    }

    suspend fun insertArticleMemory(memory: com.example.data.model.ArticleMemory) = withContext(Dispatchers.IO) {
        newsDao.insertArticleMemory(memory)
    }

    suspend fun getMemoryByArticleId(articleId: String): com.example.data.model.ArticleMemory? = withContext(Dispatchers.IO) {
        newsDao.getMemoryByArticleId(articleId)
    }

    suspend fun getRecentMemories(limit: Int = 20): List<com.example.data.model.ArticleMemory> = withContext(Dispatchers.IO) {
        newsDao.getRecentMemories(limit)
    }

    suspend fun getArticleById(articleId: String): NewsArticle? = withContext(Dispatchers.IO) {
        newsDao.getArticleById(articleId)
    }

    val tomorrowPredictions: Flow<List<com.example.data.model.TomorrowPrediction>> = newsDao.getTomorrowPredictionsFlow()

    suspend fun getCachedPredictions(): List<com.example.data.model.TomorrowPrediction> = withContext(Dispatchers.IO) {
        newsDao.getLatestTomorrowPredictions(10)
    }

    suspend fun saveTomorrowPredictions(predictions: List<com.example.data.model.TomorrowPrediction>) = withContext(Dispatchers.IO) {
        newsDao.clearTomorrowPredictions()
        newsDao.insertTomorrowPredictions(predictions)
    }
}
