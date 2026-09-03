package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.components.DotMatrixBottomNav

enum class AppTab { FEED, SAVED, SETTINGS }

@Composable
fun MainScreen(viewModel: NewsFeedViewModel) {
    var currentTab by remember { mutableStateOf(AppTab.FEED) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentTab) {
            AppTab.FEED -> {
                NewsFeedScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
            AppTab.SAVED -> {
                SavedArticlesScreen(modifier = Modifier.fillMaxSize())
            }
            AppTab.SETTINGS -> {
                SettingsScreen(modifier = Modifier.fillMaxSize())
            }
        }

        DotMatrixBottomNav(
            currentTab = currentTab,
            onTabSelected = { currentTab = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
