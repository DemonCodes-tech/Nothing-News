package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.DynamicFeed
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NdotFontFamily
import com.example.ui.screens.AppTab

@Composable
fun DotMatrixBottomNav(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                // Glassmorphism effect in Nothing OS (dark mode typical)
                .background(Color(0xFF111111).copy(alpha = 0.85f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                tab = AppTab.FEED,
                selected = currentTab == AppTab.FEED,
                icon = Icons.Outlined.DynamicFeed,
                label = "FEED",
                onClick = { onTabSelected(AppTab.FEED) }
            )
            NavItem(
                tab = AppTab.SAVED,
                selected = currentTab == AppTab.SAVED,
                icon = Icons.Outlined.BookmarkBorder,
                label = "SAVED",
                onClick = { onTabSelected(AppTab.SAVED) }
            )
            NavItem(
                tab = AppTab.SETTINGS,
                selected = currentTab == AppTab.SETTINGS,
                icon = Icons.Outlined.Settings,
                label = "CFG",
                onClick = { onTabSelected(AppTab.SETTINGS) }
            )
        }
    }
}

@Composable
private fun NavItem(
    tab: AppTab,
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .background(if (selected) Color.White.copy(alpha = 0.05f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color(0xFFE53935) else Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
            AnimatedVisibility(
                visible = selected,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        color = Color.White,
                        fontFamily = NdotFontFamily,
                        fontSize = 16.sp
                    )
                }
            }
        }
        
        // Dot-matrix active indicator below
        if (selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(bottom = 2.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    repeat(5) {
                        Box(
                            modifier = Modifier
                                .size(2.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFE53935))
                        )
                    }
                }
            }
        }
    }
}
