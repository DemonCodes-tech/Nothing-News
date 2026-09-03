package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.remote.RssParser
import com.example.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import com.example.data.model.NewsArticle

class NewsSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d("NewsSyncWorker", "Starting background sync for alerts")
            val database = AppDatabase.getDatabase(applicationContext)
            val dao = database.newsDao()
            val rssParser = RssParser()
            
            // Get current articles to compare against
            val existingArticles = dao.getAllArticles().firstOrNull() ?: emptyList()
            val existingIds = existingArticles.map { it.id }.toSet()

            // Fetch latest feeds
            val fetchedArticles = mutableListOf<NewsArticle>()
            fetchedArticles.addAll(rssParser.fetchFeed("https://www.aljazeera.com/xml/rss/all.xml", "AL JAZEERA", "PALESTINE", false))
            fetchedArticles.addAll(rssParser.fetchFeed("https://news.un.org/feed/subscribe/en/news/all/rss.xml", "UN NEWS", "WORLD", false))
            fetchedArticles.addAll(rssParser.fetchFeed("https://feeds.bbci.co.uk/news/world/rss.xml", "BBC WORLD", "WORLD", false))
            
            val newArticles = fetchedArticles.filter { !existingIds.contains(it.id) }
            
            if (newArticles.isNotEmpty()) {
                dao.insertArticles(newArticles)
                
                // Cache pruning: delete unbookmarked articles older than 7 days
                val sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
                dao.deleteOldUnbookmarkedArticles(sevenDaysAgo)
                
                // Check for alerts: breaking news or anything that heavily impacts the world
                val alerts = newArticles.filter { it.isBreaking || (it.category == "WORLD" && it.title.contains(Regex("(crisis|war|emergency|unprecedented|historic|alert|attack|disaster)", RegexOption.IGNORE_CASE))) }
                
                alerts.forEach { article ->
                    val aiAlert = com.example.util.GeminiAlertGenerator.generateAlert(article.title, article.summary)
                    val displayTitle = aiAlert?.title ?: article.title
                    val displaySummary = aiAlert?.body ?: article.summary
                    NotificationHelper.showWorldAlertNotification(
                        applicationContext,
                        displayTitle,
                        displaySummary,
                        article.id.hashCode()
                    )
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("NewsSyncWorker", "Error syncing news", e)
            Result.retry()
        }
    }
}
