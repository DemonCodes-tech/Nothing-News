package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.ui.theme.GeistFamily
import com.example.ui.theme.GeistMonoFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSettingsSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    // Quick Settings State
    var complexity by remember { mutableStateOf("Intermediate") }
    var mood by remember { mutableStateOf("Balanced") }
    var deadline by remember { mutableStateOf("15min") }
    var noiseCancellation by remember { mutableStateOf(50f) }
    var publicPulse by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0A0A0A), // Deep dark for Nothing OS
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.2f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFE53935))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "QUICK CONFIG",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = GeistMonoFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                )
            }

            SettingsDropdown("Complexity Dial", "Beginner / Intermediate / Expert", Icons.Outlined.Speed, listOf("Beginner", "Intermediate", "Expert"), complexity) { complexity = it }
            SettingsDropdown("Mood", "Deep Dive / Quick Scan / Balanced", Icons.Outlined.Mood, listOf("Deep Dive", "Quick Scan", "Balanced"), mood) { mood = it }
            SettingsDropdown("Deadline Mode", "1min / 5min / 15min / 30min", Icons.Outlined.Timer, listOf("1min", "5min", "15min", "30min"), deadline) { deadline = it }
            SettingsSliderRow("Noise Cancellation", "Filter threshold: ${noiseCancellation.toInt()}", Icons.Outlined.FilterAlt, noiseCancellation, 0f..100f, 100) { noiseCancellation = it }
            SettingsToggle("Public Pulse", "Global social sentiment overlay", Icons.Outlined.Forum, publicPulse) { publicPulse = it }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
