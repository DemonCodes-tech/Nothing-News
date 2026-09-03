package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.GeistFamily
import com.example.ui.theme.GeistMonoFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    
    // State variables for section 1
    var themeMode by remember { mutableStateOf("System") }
    var adaptiveColors by remember { mutableStateOf(true) }
    var fontSize by remember { mutableStateOf("Medium") }
    var fontFamily by remember { mutableStateOf("Geist") }
    var cardStyle by remember { mutableStateOf("Frosted Glass") }
    var reduceMotion by remember { mutableStateOf(false) }
    var homeLayout by remember { mutableStateOf("List View") }
    
    // State variables for section 2
    var refreshInterval by remember { mutableStateOf("15min") }
    var articlesPerPage by remember { mutableStateOf(30f) }
    var defaultSortOrder by remember { mutableStateOf("Relevance") }
    var showReadArticles by remember { mutableStateOf(true) }
    var autoArchive by remember { mutableStateOf("7 days") }
    var imageLoading by remember { mutableStateOf("Wi-Fi Only") }
    var openLinksIn by remember { mutableStateOf("In-App Browser") }
    var videoAutoplay by remember { mutableStateOf("Wi-Fi Only") }
    
    // State variables for section 3
    var sentimentThreshold by remember { mutableStateOf(-50f) }
    var sensationalismFilter by remember { mutableStateOf(7f) }
    var readabilityFilter by remember { mutableStateOf("All") }
    
    // State variables for section 4
    var geminiApiKey by remember { mutableStateOf("") }
    var geminiModel by remember { mutableStateOf("1.5 Flash") }
    var conversationalSearch by remember { mutableStateOf(true) }
    var audioBriefing by remember { mutableStateOf(true) }
    var voiceSelection by remember { mutableStateOf("Default") }
    var briefingLength by remember { mutableStateOf(5f) }
    var briefingTone by remember { mutableStateOf("Balanced") }
    var globalComplexity by remember { mutableStateOf("Intermediate") }
    var visualDataDecoder by remember { mutableStateOf(true) }
    var scenarioSimulator by remember { mutableStateOf(true) }
    var globalLens by remember { mutableStateOf(true) }
    var factCrossCheck by remember { mutableStateOf(true) }
    var timeMachine by remember { mutableStateOf(true) }
    var publicPulse by remember { mutableStateOf(true) }
    
    // State variables for section 5
    var secondBrain by remember { mutableStateOf(true) }
    var autoSave by remember { mutableStateOf(false) }
    var offlineVault by remember { mutableStateOf(true) }
    var offlineStorageLimit by remember { mutableStateOf(1f) } // GB
    var autoDeleteOffline by remember { mutableStateOf("30 days") }
    
    // State variables for section 6
    var weeklyReport by remember { mutableStateOf(true) }
    var reportDay by remember { mutableStateOf("Sunday") }
    var sendToEmail by remember { mutableStateOf(false) }
    var emailAddress by remember { mutableStateOf("") }
    var cognitiveTracking by remember { mutableStateOf(true) }
    var privacyMode by remember { mutableStateOf(false) }
    
    // State variables for section 7
    var trustCircles by remember { mutableStateOf(true) }
    var shareReadStatus by remember { mutableStateOf(false) }
    var discussionFuel by remember { mutableStateOf(true) }
    var newArticleAlerts by remember { mutableStateOf(true) }
    var shareAnalytics by remember { mutableStateOf(false) }
    
    // State variables for section 8
    var gamification by remember { mutableStateOf(true) }
    var dailyQuiz by remember { mutableStateOf(true) }
    var spotTheBias by remember { mutableStateOf(true) }
    var swipeSort by remember { mutableStateOf(true) }
    var readingStreaks by remember { mutableStateOf(true) }
    var achievementNotifs by remember { mutableStateOf(true) }
    
    // State variables for section 9
    var pushNotifs by remember { mutableStateOf(true) }
    var breakingNews by remember { mutableStateOf(true) }
    var savedArticleUpdates by remember { mutableStateOf(true) }
    var trustCircleAlerts by remember { mutableStateOf(true) }
    var weeklyReportReady by remember { mutableStateOf(true) }
    var audioBriefingReady by remember { mutableStateOf(true) }
    var achievementUnlocked by remember { mutableStateOf(true) }
    var publicPulseAlerts by remember { mutableStateOf(false) }
    
    // State variables for section 10
    var appLanguage by remember { mutableStateOf("System") }
    var defaultCountry by remember { mutableStateOf("Your country") }
    var timeZone by remember { mutableStateOf("System") }
    var batteryMode by remember { mutableStateOf("Balanced") }
    var analytics by remember { mutableStateOf(false) }
    var crashReporting by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 120.dp, top = 16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
                Text(
                    text = "SETTINGS",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = GeistFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 48.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "CONTROL PANEL",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = GeistMonoFamily,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        letterSpacing = 2.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search settings...", fontFamily = GeistMonoFamily, color = Color.White.copy(0.4f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(0.4f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(0.05f),
                        unfocusedContainerColor = Color.White.copy(0.02f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = GeistMonoFamily)
                )
            }
        }
        
        item {
            SettingsSection("1. Look & Feel") {
                SettingsDropdown("Theme Mode", "Switch between light/dark or system", Icons.Outlined.DarkMode, listOf("Light", "Dark", "System"), themeMode) { themeMode = it }
                SettingsToggle("Adaptive Colors", "Pull primary/accent from wallpaper", Icons.Outlined.ColorLens, adaptiveColors) { adaptiveColors = it }
                SettingsActionRow("Accent Color", "Override adaptive colors", Icons.Outlined.Palette) {}
                SettingsDropdown("Font Size", "Adjust text size across app", Icons.Outlined.FormatSize, listOf("Small", "Medium", "Large", "Extra Large"), fontSize) { fontSize = it }
                SettingsDropdown("Font Family", "System default or custom", Icons.Outlined.FontDownload, listOf("Geist", "System Default"), fontFamily) { fontFamily = it }
                SettingsDropdown("Card Style", "Frosted glass or solid performance", Icons.Outlined.Style, listOf("Frosted Glass", "Solid"), cardStyle) { cardStyle = it }
                SettingsToggle("Reduce Motion", "Disable animations for accessibility", Icons.Outlined.Animation, reduceMotion) { reduceMotion = it }
                SettingsDropdown("Home Layout", "Feed layout structure", Icons.Outlined.Dashboard, listOf("List View", "Grid View", "Compact"), homeLayout) { homeLayout = it }
            }
        }
        
        item {
            SettingsSection("2. Your News Diet") {
                SettingsDropdown("Feed Refresh Interval", "Check for new articles", Icons.Outlined.Update, listOf("Manual", "5min", "15min", "30min", "1hr"), refreshInterval) { refreshInterval = it }
                SettingsSliderRow("Articles Per Page", "Amount to load in feed: ${articlesPerPage.toInt()}", Icons.Outlined.Article, articlesPerPage, 10f..100f, 9) { articlesPerPage = it }
                SettingsDropdown("Default Sort Order", "How feed is sorted", Icons.Outlined.Sort, listOf("Relevance", "Reverse-Chronological"), defaultSortOrder) { defaultSortOrder = it }
                SettingsToggle("Show Read Articles", "Show articles already opened", Icons.Outlined.Visibility, showReadArticles) { showReadArticles = it }
                SettingsDropdown("Auto-Archive After", "Archive read articles", Icons.Outlined.Archive, listOf("Never", "1 day", "7 days", "30 days"), autoArchive) { autoArchive = it }
                SettingsDropdown("Image Loading", "When to load images", Icons.Outlined.Image, listOf("Always", "Wi-Fi Only", "Never"), imageLoading) { imageLoading = it }
                SettingsDropdown("Open Links In", "Browser preference", Icons.Outlined.OpenInBrowser, listOf("In-App Browser", "External Browser"), openLinksIn) { openLinksIn = it }
                SettingsDropdown("Video Autoplay", "When to autoplay videos", Icons.Outlined.PlayCircleOutline, listOf("On", "Wi-Fi Only", "Off"), videoAutoplay) { videoAutoplay = it }
            }
        }
        
        item {
            SettingsSection("3. Where It Comes From") {
                SettingsActionRow("Custom Sources", "RSS, Substack, YouTube, Reddit", Icons.Outlined.AddLink) {}
                SettingsActionRow("Preferred Sources", "Prioritize favorites in feed", Icons.Outlined.FavoriteBorder) {}
                SettingsActionRow("Blocked Sources", "Never see these", Icons.Outlined.Block) {}
                SettingsActionRow("Muted Keywords", "Tag cloud for noise cancellation", Icons.Outlined.FilterAlt) {}
                SettingsSliderRow("Sentiment Threshold", "Mute below: ${sentimentThreshold.toInt()}", Icons.Outlined.SentimentVeryDissatisfied, sentimentThreshold, -100f..100f, 0) { sentimentThreshold = it }
                SettingsSliderRow("Sensationalism Filter", "Mute above: ${sensationalismFilter.toInt()}", Icons.Outlined.WarningAmber, sensationalismFilter, 0f..10f, 10) { sensationalismFilter = it }
                SettingsDropdown("Readability Filter", "Filter by reading level", Icons.Outlined.MenuBook, listOf("All", "Beginner", "Intermediate", "Expert"), readabilityFilter) { readabilityFilter = it }
                SettingsActionRow("Category Toggles", "Tech, Politics, Health, etc.", Icons.Outlined.Category) {}
            }
        }

        item {
            SettingsSection("4. Artificial Intelligence") {
                SettingsInputRow("Gemini API Key", "Your Google AI Studio API key", Icons.Outlined.VpnKey, geminiApiKey) { geminiApiKey = it }
                SettingsDropdown("Gemini Model", "Model selection", Icons.Outlined.Memory, listOf("1.5 Pro", "1.5 Flash", "2.0 Flash"), geminiModel) { geminiModel = it }
                SettingsToggle("Conversational Search", "Chat-based search feature", Icons.Outlined.ChatBubbleOutline, conversationalSearch) { conversationalSearch = it }
                SettingsToggle("Audio Briefing", "Daily personalized podcasts", Icons.Outlined.Headset, audioBriefing) { audioBriefing = it }
                SettingsDropdown("Voice Selection", "TTS voice for briefings", Icons.Outlined.RecordVoiceOver, listOf("Default", "Journalist", "Casual"), voiceSelection) { voiceSelection = it }
                SettingsSliderRow("Briefing Length", "Target duration: ${briefingLength.toInt()} min", Icons.Outlined.Timer, briefingLength, 3f..15f, 12) { briefingLength = it }
                SettingsDropdown("Briefing Tone", "Style of the audio", Icons.Outlined.TheaterComedy, listOf("Concise", "Balanced", "Detailed", "Humorous"), briefingTone) { briefingTone = it }
                SettingsDropdown("Global Complexity Dial", "Overrides all AI responses", Icons.Outlined.Speed, listOf("Beginner", "Intermediate", "Expert"), globalComplexity) { globalComplexity = it }
                SettingsToggle("Visual Data Decoder", "Auto-describe charts", Icons.Outlined.BarChart, visualDataDecoder) { visualDataDecoder = it }
                SettingsToggle("Scenario Simulator", "What-If generation for news", Icons.Outlined.Science, scenarioSimulator) { scenarioSimulator = it }
                SettingsToggle("Global Lens", "International media comparison", Icons.Outlined.Public, globalLens) { globalLens = it }
                SettingsToggle("Fact Cross-Check", "Highlight contradictions", Icons.Outlined.FactCheck, factCrossCheck) { factCrossCheck = it }
                SettingsToggle("Time Machine", "Historical On This Day context", Icons.Outlined.History, timeMachine) { timeMachine = it }
                SettingsToggle("Public Pulse", "Auto-fetch social sentiment", Icons.Outlined.Forum, publicPulse) { publicPulse = it }
                SettingsActionRow("Social Platform API Keys", "X, Reddit, Apify credentials", Icons.Outlined.Key) {}
            }
        }

        item {
            SettingsSection("5. Your Memory") {
                SettingsToggle("Second Brain", "Auto knowledge extraction", Icons.Outlined.Psychology, secondBrain) { secondBrain = it }
                SettingsToggle("Auto-Save", "Save if read > 30s", Icons.Outlined.SaveAlt, autoSave) { autoSave = it }
                SettingsToggle("Offline Vault", "Pre-download for offline", Icons.Outlined.CloudDownload, offlineVault) { offlineVault = it }
                SettingsSliderRow("Offline Storage Limit", "Max storage: ${offlineStorageLimit}GB", Icons.Outlined.SdStorage, offlineStorageLimit, 0.1f..5f, 49) { offlineStorageLimit = it }
                SettingsDropdown("Auto-Delete Offline", "Cleanup after", Icons.Outlined.AutoDelete, listOf("Never", "7 days", "30 days"), autoDeleteOffline) { autoDeleteOffline = it }
                SettingsActionRow("Export Vault", "Export saved as JSON/PDF", Icons.Outlined.ImportExport) {}
                SettingsDangerButton("Clear Vault", Icons.Outlined.DeleteForever) {}
            }
        }

        item {
            SettingsSection("6. Self-Awareness") {
                SettingsToggle("Weekly Report", "Generate Mental Diet report", Icons.Outlined.Assessment, weeklyReport) { weeklyReport = it }
                SettingsDropdown("Report Day", "When to generate", Icons.Outlined.Today, listOf("Monday", "Friday", "Sunday"), reportDay) { reportDay = it }
                SettingsToggle("Send to Email", "Email weekly report", Icons.Outlined.Email, sendToEmail) { sendToEmail = it }
                if (sendToEmail) {
                    SettingsInputRow("Email Address", "For weekly reports", null, emailAddress) { emailAddress = it }
                }
                SettingsToggle("Cognitive Tracking", "Track reading habits", Icons.Outlined.QueryStats, cognitiveTracking) { cognitiveTracking = it }
                SettingsToggle("Privacy Mode", "Disable all tracking", Icons.Outlined.Security, privacyMode) { privacyMode = it }
                SettingsDangerButton("Clear Reading History", Icons.Outlined.DeleteSweep) {}
            }
        }

        item {
            SettingsSection("7. Your People") {
                SettingsToggle("Trust Circles", "Social sharing with trusted friends", Icons.Outlined.Groups, trustCircles) { trustCircles = it }
                SettingsActionRow("My Circle", "Add/remove contacts", Icons.Outlined.PersonAdd) {}
                SettingsToggle("Share Read Status", "Circle sees what you read", Icons.Outlined.Visibility, shareReadStatus) { shareReadStatus = it }
                SettingsToggle("Discussion Fuel", "Auto-generate questions", Icons.Outlined.TipsAndUpdates, discussionFuel) { discussionFuel = it }
                SettingsToggle("New Article Alerts", "When circle shares", Icons.Outlined.NotificationsActive, newArticleAlerts) { newArticleAlerts = it }
                SettingsToggle("Share Analytics", "Aggregated insights", Icons.Outlined.Analytics, shareAnalytics) { shareAnalytics = it }
            }
        }

        item {
            SettingsSection("8. Fun & Engagement") {
                SettingsToggle("Gamification", "Turn on gamification", Icons.Outlined.SportsEsports, gamification) { gamification = it }
                SettingsToggle("Daily Quiz", "Daily news quiz", Icons.Outlined.Quiz, dailyQuiz) { dailyQuiz = it }
                SettingsToggle("Spot the Bias", "Bias detection mini-game", Icons.Outlined.GpsFixed, spotTheBias) { spotTheBias = it }
                SettingsToggle("Swipe Sort", "Swipe categorization", Icons.Outlined.Swipe, swipeSort) { swipeSort = it }
                SettingsToggle("Reading Streaks", "Track daily streak", Icons.Outlined.LocalFireDepartment, readingStreaks) { readingStreaks = it }
                SettingsToggle("Achievement Notifications", "Get notified", Icons.Outlined.EmojiEvents, achievementNotifs) { achievementNotifs = it }
                SettingsDangerButton("Reset Progress", Icons.Outlined.RestartAlt) {}
            }
        }

        item {
            SettingsSection("9. What Alerts You") {
                SettingsToggle("Push Notifications", "Enable all push", Icons.Outlined.Notifications, pushNotifs) { pushNotifs = it }
                SettingsToggle("Breaking News", "Major breaking stories", Icons.Outlined.Campaign, breakingNews) { breakingNews = it }
                SettingsToggle("Saved Article Updates", "Updates to saved", Icons.Outlined.Update, savedArticleUpdates) { savedArticleUpdates = it }
                SettingsToggle("Trust Circle Alerts", "Friend shares article", Icons.Outlined.GroupAdd, trustCircleAlerts) { trustCircleAlerts = it }
                SettingsToggle("Weekly Report Ready", "Mirror report ready", Icons.Outlined.Summarize, weeklyReportReady) { weeklyReportReady = it }
                SettingsToggle("Audio Briefing Ready", "Daily audio ready", Icons.Outlined.AudioFile, audioBriefingReady) { audioBriefingReady = it }
                SettingsToggle("Achievement Unlocked", "Earn gamification", Icons.Outlined.EmojiEvents, achievementUnlocked) { achievementUnlocked = it }
                SettingsToggle("Public Pulse Alerts", "Sentiment shift", Icons.Outlined.Forum, publicPulseAlerts) { publicPulseAlerts = it }
                SettingsActionRow("Quiet Hours", "Do not disturb (11 PM - 7 AM)", Icons.Outlined.DoNotDisturb) {}
            }
        }

        item {
            SettingsSection("10. App & Device") {
                SettingsDropdown("App Language", "Preferred language", Icons.Outlined.Language, listOf("System", "English", "Spanish", "French"), appLanguage) { appLanguage = it }
                SettingsDropdown("Default Country", "Local news preference", Icons.Outlined.Flag, listOf("Your country", "US", "UK", "Global"), defaultCountry) { defaultCountry = it }
                SettingsDropdown("Time Zone", "Override system timezone", Icons.Outlined.Schedule, listOf("System", "UTC", "EST", "PST"), timeZone) { timeZone = it }
                SettingsActionRow("Storage Usage", "Show usage & clear cache", Icons.Outlined.Storage) {}
                SettingsActionRow("Data Usage", "Network usage by app", Icons.Outlined.DataUsage) {}
                SettingsDropdown("Battery Mode", "Performance / Power Saver", Icons.Outlined.BatteryStd, listOf("Performance", "Balanced", "Power Saver"), batteryMode) { batteryMode = it }
                SettingsToggle("Analytics", "Share anonymous usage", Icons.Outlined.Analytics, analytics) { analytics = it }
                SettingsToggle("Crash Reporting", "Auto report crashes", Icons.Outlined.BugReport, crashReporting) { crashReporting = it }
                SettingsActionRow("App Version", "1.0.42 (Build 2026)", Icons.Outlined.Info) {}
                SettingsActionRow("Check for Updates", "Up to date", Icons.Outlined.SystemUpdate) {}
            }
        }

        item {
            SettingsSection("11. Get Help") {
                SettingsActionRow("Quick Start Guide", "Walkthrough of all features", Icons.Outlined.HelpOutline) {}
                SettingsActionRow("FAQ", "Frequently asked questions", Icons.Outlined.QuestionAnswer) {}
                SettingsActionRow("Feature Tutorials", "Video/text guides", Icons.Outlined.OndemandVideo) {}
                SettingsActionRow("API Status", "Check Gemini/X/Reddit", Icons.Outlined.Dns) {}
                SettingsActionRow("Export Settings", "Export JSON backup", Icons.Outlined.FileDownload) {}
                SettingsActionRow("Import Settings", "Import backup", Icons.Outlined.FileUpload) {}
                SettingsDangerButton("Reset All Settings", Icons.Outlined.SettingsBackupRestore) {}
                SettingsDangerButton("Delete All Data", Icons.Outlined.DeleteForever) {}
            }
        }
    }
}
