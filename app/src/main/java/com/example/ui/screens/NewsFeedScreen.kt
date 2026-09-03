package com.example.ui.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SoftwareMaterialHeader
import com.example.ui.components.MinimalistReaderSheet
import com.example.ui.components.NothingNewsCard
import com.example.ui.components.NothingPillTab
import com.example.ui.components.PalestineHumanitarianMonitorWidget
import com.example.ui.theme.NothingDarkBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsFeedScreen(
    viewModel: NewsFeedViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val audioSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSwipeGameOpen by remember { mutableStateOf(false) }
    var isNightBriefOpen by remember { mutableStateOf(false) }
    var audioBriefingArticle by remember { mutableStateOf<com.example.data.model.NewsArticle?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()
    var isQuickSettingsOpen by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.syncMessage) {
        uiState.syncMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
            // Nothing OS Top Dot-Matrix Ticker
            SoftwareMaterialHeader(
                title = "NOTHING (NEWS)",
                liveCount = uiState.articles.size,
                onRefresh = { viewModel.syncFeeds() },
                onSwipeModeClick = { isSwipeGameOpen = true },
                onNightBriefClick = { isNightBriefOpen = true },
                onLongPress = { isQuickSettingsOpen = true },
                isNightTime = uiState.isNightActive
            )

            // Night Brief Banner (Appears at 10 PM / 22:00 or when toggled)
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                com.example.ui.components.NightBriefBanner(
                    isNightTime = uiState.isNightActive,
                    cachedPredictionsCount = uiState.tomorrowPredictions.size,
                    onClick = { isNightBriefOpen = true },
                    onForceToggleNight = { viewModel.toggleSimulateNight() }
                )
            }

            // Time Compression Slider ("Time until my next meeting")
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                com.example.ui.components.TimeCompressionSlider(
                    selectedMinutes = uiState.timeCompressionMinutes,
                    isCompressing = uiState.isCompressingTime,
                    onMinutesChanged = { viewModel.setTimeCompression(it) }
                )
            }

            // The Mood Dial
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                com.example.ui.components.MoodDial(
                    currentMood = uiState.currentMood,
                    onMoodSelected = { viewModel.setMood(it) }
                )
            }

            // Search Bar
            Box(
                modifier = Modifier.background(androidx.compose.ui.graphics.Color(0xEB000000))
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.background(androidx.compose.ui.graphics.Color(0xEB000000)).size(16.dp)
                    )

                    BasicTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier.background(androidx.compose.ui.graphics.Color(0xEB000000))
                            .weight(1f)
                            .testTag("search_input"),
                        textStyle = TextStyle(
                            fontFamily = com.example.ui.theme.NdotFontFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        decorationBox = { innerTextField ->
                            if (uiState.searchQuery.isEmpty()) {
                                Text(
                                    text = "SEARCH DISPATCHES...",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = com.example.ui.theme.NdotFontFamily,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )

                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.setSearchQuery("") },
                            modifier = Modifier.background(androidx.compose.ui.graphics.Color(0xEB000000)).size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.background(androidx.compose.ui.graphics.Color(0xEB000000)).size(14.dp)
                            )
                        }
                    }
                }
            }

            // Segmented Feed Tabs (Horizontal scrollable pill row)
            val tabScrollState = rememberScrollState()
            Row(
                modifier = Modifier.background(androidx.compose.ui.graphics.Color(0xEB000000))
                    .fillMaxWidth()
                    .horizontalScroll(tabScrollState)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NothingPillTab(
                    text = "ALL",
                    isSelected = uiState.activeTab == FeedTab.ALL,
                    onClick = { viewModel.setTab(FeedTab.ALL) }
                )
                NothingPillTab(
                    text = "PALESTINE",
                    isSelected = uiState.activeTab == FeedTab.PALESTINE,
                    onClick = { viewModel.setTab(FeedTab.PALESTINE) },
                    badgeCount = uiState.palestineCount,
                    isRedAccent = true
                )
                NothingPillTab(
                    text = "WORLD",
                    isSelected = uiState.activeTab == FeedTab.WORLD,
                    onClick = { viewModel.setTab(FeedTab.WORLD) },
                    badgeCount = uiState.worldCount
                )
                NothingPillTab(
                    text = "LIVE ALERTS",
                    isSelected = uiState.activeTab == FeedTab.LIVE,
                    onClick = { viewModel.setTab(FeedTab.LIVE) }
                )
                NothingPillTab(
                    text = "CRISIS HUB",
                    isSelected = uiState.activeTab == FeedTab.CONTEXT,
                    onClick = { viewModel.setTab(FeedTab.CONTEXT) }
                )
                NothingPillTab(
                    text = "SAVED",
                    isSelected = uiState.activeTab == FeedTab.SAVED,
                    onClick = { viewModel.setTab(FeedTab.SAVED) },
                    badgeCount = uiState.bookmarkedCount
                )
            }

            // Category Filter Pills
            val categories = listOf("ALL", "HUMANITARIAN", "DIPLOMACY", "ANALYSIS", "BREAKING")
            val catScrollState = rememberScrollState()
            Row(
                modifier = Modifier.background(androidx.compose.ui.graphics.Color(0xEB000000))
                    .fillMaxWidth()
                    .horizontalScroll(catScrollState)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = uiState.selectedCategory.equals(category, ignoreCase = true)
                    Box(
                        modifier = Modifier.background(androidx.compose.ui.graphics.Color(0xEB000000))
                            .clip(RoundedCornerShape(4.dp))
                            .border(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(4.dp)
                            )
                            .background(if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background)
                            .clickable { viewModel.setCategory(category) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("cat_filter_$category")
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            modifier = Modifier.background(androidx.compose.ui.graphics.Color(0xEB000000)).padding(1.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.background(androidx.compose.ui.graphics.Color(0xEB000000)).height(4.dp))

            // Main Content Area with Pull-to-Refresh
            PullToRefreshBox(
                isRefreshing = uiState.isSyncing,
                onRefresh = { viewModel.syncFeeds() },
                state = pullToRefreshState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                indicator = {
                    com.example.ui.components.DotMatrixRefreshIndicator(
                        state = pullToRefreshState,
                        isRefreshing = uiState.isSyncing,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Show Palestine Crisis Monitor Widget if on PALESTINE or CRISIS HUB tab
                    if (uiState.activeTab == FeedTab.PALESTINE || uiState.activeTab == FeedTab.CONTEXT || uiState.activeTab == FeedTab.ALL) {
                        item(key = "palestine_widget") {
                            PalestineHumanitarianMonitorWidget(
                                modifier = Modifier.background(androidx.compose.ui.graphics.Color(0xEB000000)).padding(bottom = 4.dp)
                            )
                        }
                    }

                    if (uiState.articles.isEmpty()) {
                        item(key = "empty_state") {
                            Box(
                                modifier = Modifier.background(androidx.compose.ui.graphics.Color(0xEB000000))
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "[ NO DISPATCHES FOUND ]",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontFamily = com.example.ui.theme.NdotFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                    Text(
                                        text = if (uiState.searchQuery.isNotEmpty()) "Try clearing your search query" else "Check other feed tabs or tap [ SYNC ]",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.6f),
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        itemsIndexed(
                            items = uiState.articles,
                            key = { _, article -> article.id }
                        ) { index, article ->
                            NothingNewsCard(
                                article = article,
                                index = index,
                                onArticleClick = { viewModel.selectArticle(it) },
                                onBookmarkToggle = { viewModel.toggleBookmark(it) },
                                onListen = { audioBriefingArticle = it },
                                onShare = { art ->
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(android.content.Intent.EXTRA_TITLE, art.title)
                                        putExtra(android.content.Intent.EXTRA_TEXT, "${art.title}\n\n${art.summary}\n\nVia Nothing News [${art.source}]")
                                        type = "text/plain"
                                    }
                                    context.startActivity(android.content.Intent.createChooser(sendIntent, "Share Dispatch"))
                                },
                                mood = uiState.currentMood,
                                timeCompressionMinutes = uiState.timeCompressionMinutes,
                                compressedSummary = uiState.compressedSummaries[article.id]
                            )
                        }
                    }
                }
            }
        }

        // Subtle retro scanning line animation across the screen when pulling to refresh
        val isPullRefreshing = uiState.isSyncing || pullToRefreshState.distanceFraction > 0.05f
        com.example.ui.components.RetroScanlineOverlay(
            visible = isPullRefreshing
        )
    }

        // Full Text Minimalist Reader Modal Sheet
        if (uiState.selectedArticle != null) {
            MinimalistReaderSheet(
                article = uiState.selectedArticle,
                sheetState = sheetState,
                isPlayingAudio = uiState.isPlayingAudio,
                relatedMemories = uiState.relatedMemories,
                onDismiss = {
                    viewModel.stopAudio()
                    viewModel.selectArticle(null)
                },
                onBookmarkToggle = { viewModel.toggleBookmark(it) },
                onToggleAudio = { audioBriefingArticle = it }
            )
        }

        audioBriefingArticle?.let { article ->
            com.example.ui.components.AudioBriefingSheet(
                article = article,
                sheetState = audioSheetState,
                onDismiss = { audioBriefingArticle = null }
            )
        }

        if (isNightBriefOpen) {
            com.example.ui.components.TomorrowsHeadlinesSheet(
                predictions = uiState.tomorrowPredictions,
                isPredicting = uiState.isPredictingTomorrow,
                onPredictRequest = { viewModel.generateTomorrowPredictions() },
                onDismiss = { isNightBriefOpen = false }
            )
        }

        androidx.compose.animation.AnimatedVisibility(
            visible = isSwipeGameOpen,
            enter = androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            com.example.ui.components.SwipeSortGame(
                articles = uiState.articles.filter { !it.isBookmarked },
                onDismiss = { isSwipeGameOpen = false },
                onBookmark = { viewModel.toggleBookmark(it) }
            )
        }

        if (isQuickSettingsOpen) {
            com.example.ui.components.QuickSettingsSheet(
                onDismiss = { isQuickSettingsOpen = false }
            )
        }
    }
}
