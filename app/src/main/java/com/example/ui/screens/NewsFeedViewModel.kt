package com.example.ui.screens

import com.example.util.NotificationHelper
import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.NewsArticle
import com.example.data.model.TomorrowPrediction
import com.example.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import com.example.util.GeminiMemoryExtractor
import org.json.JSONArray
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

enum class FeedTab(val title: String) {
    ALL("ALL"),
    PALESTINE("PALESTINE"),
    WORLD("WORLD"),
    LIVE("LIVE ALERTS"),
    SAVED("SAVED"),
    CONTEXT("CRISIS HUB")
}

enum class NewsMood(val title: String) {
    QUICK_SCAN("QUICK SCAN"),
    BALANCED("BALANCED"),
    DEEP_DIVE("DEEP DIVE")
}

data class FilterState(
    val tab: FeedTab = FeedTab.ALL,
    val category: String = "ALL",
    val query: String = "",
    val mood: NewsMood = NewsMood.BALANCED
)

data class NewsUiState(
    val articles: List<NewsArticle> = emptyList(),
    val activeTab: FeedTab = FeedTab.ALL,
    val selectedCategory: String = "ALL",
    val searchQuery: String = "",
    val isSyncing: Boolean = false,
    val selectedArticle: NewsArticle? = null,
    val isPlayingAudio: Boolean = false,
    val syncMessage: String? = null,
    val palestineCount: Int = 0,
    val worldCount: Int = 0,
    val bookmarkedCount: Int = 0,
    val currentMood: NewsMood = NewsMood.BALANCED,
    val relatedMemories: List<RelatedMemory> = emptyList(),
    val timeCompressionMinutes: Int? = null,
    val isCompressingTime: Boolean = false,
    val compressedSummaries: Map<String, String> = emptyMap(),
    val tomorrowPredictions: List<TomorrowPrediction> = emptyList(),
    val isPredictingTomorrow: Boolean = false,
    val isNightActive: Boolean = false,
    val isNightSimulated: Boolean = false
)

class NewsFeedViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NewsRepository
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _activeTab = MutableStateFlow(FeedTab.ALL)
    val activeTab = _activeTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _currentMood = MutableStateFlow(NewsMood.BALANCED)
    val currentMood = _currentMood.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    private val _selectedArticle = MutableStateFlow<NewsArticle?>(null)
    val selectedArticle = _selectedArticle.asStateFlow()

    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio = _isPlayingAudio.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage = _syncMessage.asStateFlow()

    private val _timeCompressionMinutes = MutableStateFlow<Int?>(null)
    val timeCompressionMinutes = _timeCompressionMinutes.asStateFlow()

    private val _isCompressingTime = MutableStateFlow(false)
    val isCompressingTime = _isCompressingTime.asStateFlow()

    private val _compressedSummaries = MutableStateFlow<Map<String, String>>(emptyMap())
    val compressedSummaries = _compressedSummaries.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = NewsRepository(db.newsDao())

        initTts(application)

        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
            syncFeeds()
            // Pre-seed baseline predictions if cache is empty
            val cached = repository.getCachedPredictions()
            if (cached.isEmpty()) {
                val baseline = com.example.util.GeminiTomorrowPredictor.predictTomorrowHeadlines(emptyList())
                repository.saveTomorrowPredictions(baseline)
            }
        }
    }

    private fun initTts(context: Application) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isPlayingAudio.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isPlayingAudio.value = false
                    }

                    override fun onError(utteranceId: String?) {
                        _isPlayingAudio.value = false
                    }
                })
                isTtsReady = true
            }
        }
    }

    data class TimeCompressionData(
        val minutes: Int? = null,
        val isCompressing: Boolean = false,
        val summaries: Map<String, String> = emptyMap()
    )

    private val timeCompressionFlow = combine(
        _timeCompressionMinutes,
        _isCompressingTime,
        _compressedSummaries
    ) { minutes, compressing, summaries ->
        TimeCompressionData(minutes, compressing, summaries)
    }

    data class NightBriefData(
        val predictions: List<TomorrowPrediction> = emptyList(),
        val isPredicting: Boolean = false,
        val isSimulated: Boolean = false
    )

    private val _isPredictingTomorrow = MutableStateFlow(false)
    val isPredictingTomorrow = _isPredictingTomorrow.asStateFlow()

    private val _isNightSimulated = MutableStateFlow(false)
    val isNightSimulated = _isNightSimulated.asStateFlow()

    private val nightBriefFlow = combine(
        repository.tomorrowPredictions,
        _isPredictingTomorrow,
        _isNightSimulated
    ) { predictions, predicting, simulated ->
        NightBriefData(predictions, predicting, simulated)
    }

    private val auxiliaryFeaturesFlow = combine(
        timeCompressionFlow,
        nightBriefFlow
    ) { time, night -> Pair(time, night) }

    private val statusFlow = combine(_isSyncing, _isPlayingAudio) { syncing, audio -> Pair(syncing, audio) }

    private val _relatedMemories = MutableStateFlow<List<RelatedMemory>>(emptyList())
    private val selectionFlow = combine(_selectedArticle, _relatedMemories) { art, mems -> Pair(art, mems) }

    private val filterFlow: StateFlow<FilterState> = combine(
        _activeTab,
        _selectedCategory,
        _searchQuery,
        _currentMood
    ) { tab, category, query, mood ->
        FilterState(tab, category, query, mood)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FilterState()
    )

    val uiState: StateFlow<NewsUiState> = combine(
        repository.allArticles,
        filterFlow,
        statusFlow,
        selectionFlow,
        auxiliaryFeaturesFlow
    ) { allArticles: List<NewsArticle>, filter: FilterState, status: Pair<Boolean, Boolean>, selection: Pair<NewsArticle?, List<RelatedMemory>>, aux: Pair<TimeCompressionData, NightBriefData> ->
        val timeComp = aux.first
        val nightBrief = aux.second
        val syncing = status.first
        val playingAudio = status.second
        val selectedArt = selection.first
        val relatedMem = selection.second
        val palCount = allArticles.count { it.isPalestine }
        val worCount = allArticles.count { !it.isPalestine }
        val bmkCount = allArticles.count { it.isBookmarked }

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val isActualNight = currentHour >= 22 || currentHour < 6
        val isNightActive = isActualNight || nightBrief.isSimulated

        // 1. Filter by tab
        val tabFiltered = when (filter.tab) {
            FeedTab.ALL -> allArticles
            FeedTab.PALESTINE -> allArticles.filter { it.isPalestine }
            FeedTab.WORLD -> allArticles.filter { !it.isPalestine }
            FeedTab.LIVE -> allArticles.filter { it.isLive || it.isBreaking }
            FeedTab.SAVED -> allArticles.filter { it.isBookmarked }
            FeedTab.CONTEXT -> allArticles.filter { it.isPalestine }
        }

        // 2. Filter by category
        val categoryFiltered = if (filter.category == "ALL") {
            tabFiltered
        } else {
            tabFiltered.filter { it.category.equals(filter.category, ignoreCase = true) }
        }

        // 3. Filter by search query
        val searchedArticles = if (filter.query.isBlank()) {
            categoryFiltered
        } else {
            categoryFiltered.filter {
                it.title.contains(filter.query, ignoreCase = true) ||
                it.summary.contains(filter.query, ignoreCase = true) ||
                it.fullContent.contains(filter.query, ignoreCase = true) ||
                it.source.contains(filter.query, ignoreCase = true) ||
                it.location.contains(filter.query, ignoreCase = true)
            }
        }

        // 4. Sort by Mood
        val finalArticles = when (filter.mood) {
            NewsMood.BALANCED -> searchedArticles // Assuming already sorted by latest in DAO
            NewsMood.DEEP_DIVE -> searchedArticles.sortedByDescending { it.fullContent.length } // Longest first
            NewsMood.QUICK_SCAN -> searchedArticles.sortedBy { it.fullContent.length } // Shortest first
        }

        NewsUiState(
            articles = finalArticles,
            activeTab = filter.tab,
            selectedCategory = filter.category,
            searchQuery = filter.query,
            isSyncing = syncing,
            selectedArticle = selectedArt,
            isPlayingAudio = playingAudio,
            syncMessage = _syncMessage.value,
            palestineCount = palCount,
            worldCount = worCount,
            bookmarkedCount = bmkCount,
            currentMood = filter.mood,
            relatedMemories = relatedMem,
            timeCompressionMinutes = timeComp.minutes,
            isCompressingTime = timeComp.isCompressing,
            compressedSummaries = timeComp.summaries,
            tomorrowPredictions = nightBrief.predictions,
            isPredictingTomorrow = nightBrief.isPredicting,
            isNightActive = isNightActive,
            isNightSimulated = nightBrief.isSimulated
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NewsUiState()
    )

    fun toggleSimulateNight() {
        _isNightSimulated.value = !_isNightSimulated.value
    }

    fun generateTomorrowPredictions() {
        viewModelScope.launch {
            _isPredictingTomorrow.value = true
            try {
                val currentArticles = repository.allArticles.first()
                val predictions = com.example.util.GeminiTomorrowPredictor.predictTomorrowHeadlines(currentArticles)
                if (predictions.isNotEmpty()) {
                    repository.saveTomorrowPredictions(predictions)
                    _syncMessage.value = "🌙 Tomorrow's Headlines updated & cached for morning"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _syncMessage.value = "Failed to generate tomorrow's predictions"
            } finally {
                _isPredictingTomorrow.value = false
            }
        }
    }

    fun setTimeCompression(minutes: Int?) {
        _timeCompressionMinutes.value = minutes
        if (minutes != null) {
            val currentArticles = uiState.value.articles
            if (currentArticles.isNotEmpty()) {
                // 1. Instant local algorithmic compression so the feed re-renders with zero latency
                val instantMap = currentArticles.associate { art ->
                    art.id to com.example.util.GeminiTimeCompressor.getInstantLocalSummary(art, minutes)
                }
                _compressedSummaries.value = _compressedSummaries.value + instantMap

                // 2. Batch-process with Gemini 1.5 Flash
                viewModelScope.launch {
                    _isCompressingTime.value = true
                    try {
                        val aiSummaries = com.example.util.GeminiTimeCompressor.compressArticlesBatch(currentArticles, minutes)
                        if (aiSummaries.isNotEmpty()) {
                            _compressedSummaries.value = _compressedSummaries.value + aiSummaries
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        _isCompressingTime.value = false
                    }
                }
            }
        }
    }

    fun setTab(tab: FeedTab) {
        _activeTab.value = tab
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setMood(mood: NewsMood) {
        _currentMood.value = mood
    }

    fun selectArticle(article: NewsArticle?) {
        _selectedArticle.value = article
        if (article != null) {
            viewModelScope.launch {
                repository.recordArticleRead(article.id)
                
                // Perform similarity search against local DB
                val recentMemories = repository.getRecentMemories(50)
                val related = mutableListOf<RelatedMemory>()
                
                // Simple keyword/entity overlap simulation
                val articleWords = article.fullContent.lowercase().split(Regex("\\W+")).toSet()
                
                for (memory in recentMemories) {
                    if (memory.articleId == article.id) continue
                    
                    val entitiesArr = org.json.JSONArray(memory.entitiesJson)
                    var matchCount = 0
                    for (i in 0 until entitiesArr.length()) {
                        val entity = entitiesArr.optString(i).lowercase()
                        if (articleWords.any { it.contains(entity) || entity.contains(it) && it.length > 3 }) {
                            matchCount++
                        }
                    }
                    
                    if (matchCount > 0) {
                        val factsArr = org.json.JSONArray(memory.factsJson)
                        if (factsArr.length() > 0) {
                            val savedArticle = repository.getArticleById(memory.articleId)
                            val title = savedArticle?.title ?: "Saved Dispatch"
                            related.add(RelatedMemory(title, factsArr.optString(0), matchCount.toDouble()))
                        }
                    }
                }
                
                _relatedMemories.value = related.sortedByDescending { it.similarityScore }.take(3)
            }
        } else {
            _relatedMemories.value = emptyList()
        }
    }

    fun toggleBookmark(article: NewsArticle) {
        viewModelScope.launch {
            repository.toggleBookmark(article.id, article.isBookmarked)
            if (!article.isBookmarked) {
                NotificationHelper.showPinnedNotification(getApplication(), article.title)
                // Extract Memory
                launch(kotlinx.coroutines.Dispatchers.IO) {
                    val memory = GeminiMemoryExtractor.extractMemory(article.id, article.fullContent)
                    if (memory != null) {
                        repository.insertArticleMemory(memory)
                    }
                }
            }
            if (_selectedArticle.value?.id == article.id) {
                _selectedArticle.value = _selectedArticle.value?.copy(isBookmarked = !article.isBookmarked)
            }
        }
    }

    fun syncFeeds() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "SYNCING LIVE OPEN FEEDS..."
            val result = repository.syncLiveFeeds()
            _isSyncing.value = false
            result.onSuccess { count ->
                _syncMessage.value = if (count > 0) "SYNCED +$count DISPATCHES" else "DISPATCHES UP TO DATE"
            }.onFailure {
                _syncMessage.value = "ONLINE SYNC FAILED — RUNNING OFFLINE CACHE"
            }
        }
    }

    fun toggleAudio(article: NewsArticle) {
        if (_isPlayingAudio.value) {
            stopAudio()
        } else {
            speakArticle(article)
        }
    }

    private fun speakArticle(article: NewsArticle) {
        if (!isTtsReady || tts == null) return
        val speechText = "${article.title}. Published by ${article.source}. ${article.summary}. ${article.fullContent}"
        tts?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "news_tts_${article.id}")
        _isPlayingAudio.value = true
    }

    fun stopAudio() {
        tts?.stop()
        _isPlayingAudio.value = false
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
