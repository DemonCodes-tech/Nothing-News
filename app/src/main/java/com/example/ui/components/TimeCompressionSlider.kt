package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GeistMonoFamily
import kotlin.math.roundToInt

val TIME_STOPS = listOf(1, 5, 15, 30)

@Composable
fun TimeCompressionSlider(
    selectedMinutes: Int?,
    isCompressing: Boolean,
    onMinutesChanged: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    // 0 -> 1 min, 1 -> 5 min, 2 -> 15 min, 3 -> 30 min
    val currentSliderValue = remember(selectedMinutes) {
        when (selectedMinutes) {
            1 -> 0f
            5 -> 1f
            15 -> 2f
            30 -> 3f
            else -> 0f
        }
    }

    var sliderPosition by remember(currentSliderValue) { mutableFloatStateOf(currentSliderValue) }
    val isActive = selectedMinutes != null

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(
                1.dp,
                if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Time Compression",
                        tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "TIME UNTIL MY NEXT MEETING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = GeistMonoFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 0.8.sp
                        )
                    )
                }

                // Active badge / Toggle button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else Color.White.copy(alpha = 0.06f)
                        )
                        .border(
                            1.dp,
                            if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable {
                            if (isActive) onMinutesChanged(null) else onMinutesChanged(1)
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isActive) "[ ${selectedMinutes}M ACTIVE ]" else "[ OFF ]",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = GeistMonoFamily,
                            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle Description
            val subtitleText = when (selectedMinutes) {
                1 -> "1-MIN MODE // 15 WORDS (1 HEADLINE + 1 PUNCHY SENTENCE)"
                5 -> "5-MIN MODE // 75 WORDS (ACTIONABLE DIGEST)"
                15 -> "15-MIN MODE // 225 WORDS (EXECUTIVE BRIEFING)"
                30 -> "30-MIN MODE // 450 WORDS (DEEP-DIVE & BACKGROUNDERS)"
                else -> "SLIDE TO INSTANTLY COMPRESS FEED FOR YOUR AVAILABLE TIME"
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isCompressing) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha))
                    )
                }
                Text(
                    text = if (isCompressing) "⚡ GEMINI 1.5 FLASH: COMPRESSING FEED..." else subtitleText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = GeistMonoFamily,
                        color = if (isCompressing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Slider
            Slider(
                value = sliderPosition,
                onValueChange = { pos ->
                    sliderPosition = pos
                },
                onValueChangeFinished = {
                    val index = sliderPosition.roundToInt().coerceIn(0, 3)
                    sliderPosition = index.toFloat()
                    onMinutesChanged(TIME_STOPS[index])
                },
                valueRange = 0f..3f,
                steps = 2,
                colors = SliderDefaults.colors(
                    thumbColor = if (isActive) MaterialTheme.colorScheme.primary else Color.White,
                    activeTrackColor = if (isActive) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.6f),
                    inactiveTrackColor = Color.White.copy(alpha = 0.15f),
                    activeTickColor = Color.Black,
                    inactiveTickColor = Color.White.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Segmented clickable stop labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TIME_STOPS.forEachIndexed { index, minutes ->
                    val isSelected = selectedMinutes == minutes
                    val words = minutes * 15

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else Color.White.copy(alpha = 0.03f)
                            )
                            .border(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else Color.White.copy(alpha = 0.08f),
                                RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                sliderPosition = index.toFloat()
                                onMinutesChanged(minutes)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${minutes} MIN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = GeistMonoFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = "${words}w",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = GeistMonoFamily,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
