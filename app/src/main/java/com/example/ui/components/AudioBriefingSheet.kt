package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NewsArticle
import com.example.ui.theme.GeistFamily
import com.example.ui.theme.GeistMonoFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioBriefingSheet(
    article: NewsArticle,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    var selectedTone by remember { mutableStateOf("ANALYTICAL") }
    var selectedVoice by remember { mutableStateOf("NOVA (FEMALE)") }
    var isDownload by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableFloatStateOf(5f) } // Duration in minutes

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null,
        modifier = Modifier.fillMaxHeight(0.85f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xEB000000))
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PODCAST GENERATION",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = GeistMonoFamily,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.1f))
                            .size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = GeistFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    maxLines = 2,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // The "Gooey" Play Button
                GooeyPlayButton(
                    isPlaying = isPlaying,
                    onClick = { isPlaying = !isPlaying }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Waveform
                WaveformVisualizer(isPlaying = isPlaying)

                Spacer(modifier = Modifier.height(48.dp))

                // Settings: Voice & Tone
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Voice Selection (Glass Dropdown Fake)
                    GlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedVoice = if (selectedVoice == "NOVA (FEMALE)") "ECHO (MALE)" else "NOVA (FEMALE)" }
                    ) {
                        Column {
                            Text("VOICE", style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily, color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(selectedVoice, style = MaterialTheme.typography.bodySmall.copy(fontFamily = GeistMonoFamily, color = MaterialTheme.colorScheme.onSurface))
                        }
                    }

                    // Download / Stream Toggle
                    GlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isDownload = !isDownload }
                    ) {
                        Column {
                            Text("MODE", style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily, color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(if (isDownload) MaterialTheme.colorScheme.primary else Color.Gray))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isDownload) "OFFLINE" else "STREAM", style = MaterialTheme.typography.bodySmall.copy(fontFamily = GeistMonoFamily, color = MaterialTheme.colorScheme.onSurface))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tone Toggles
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("NARRATIVE TONE", style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily, color = MaterialTheme.colorScheme.onSurfaceVariant))
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("ANALYTICAL", "CASUAL", "URGENT").forEach { tone ->
                                val isSel = tone == selectedTone
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                        .clickable { selectedTone = tone }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = tone,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = GeistMonoFamily,
                                            color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Duration Slider
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TARGET DURATION", style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily, color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Text("${sliderValue.toInt()} MIN", style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily, color = MaterialTheme.colorScheme.primary))
                        }
                        Slider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            valueRange = 1f..15f,
                            steps = 14,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GooeyPlayButton(isPlaying: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "gooey")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale1"
    )
    val scale2 by infiniteTransition.animateFloat(
        initialValue = 1.1f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale2"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = Modifier
            .size(100.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Blob 1
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = if (isPlaying) scale1 else 1f
                    scaleY = if (isPlaying) scale1 else 1f
                    rotationZ = if (isPlaying) rotation else 0f
                }
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 30.dp, bottomEnd = 40.dp, bottomStart = 20.dp))
                .background(primaryColor.copy(alpha = 0.6f))
        )
        // Blob 2
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = if (isPlaying) scale2 else 1f
                    scaleY = if (isPlaying) scale2 else 1f
                    rotationZ = if (isPlaying) -rotation * 1.2f else 0f
                }
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 40.dp, bottomEnd = 30.dp, bottomStart = 40.dp))
                .background(tertiaryColor.copy(alpha = 0.6f))
        )
        
        // Solid Center
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = "Play/Pause",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun WaveformVisualizer(isPlaying: Boolean) {
    val barCount = 30
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    
    val heights = (0 until barCount).map { i ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = if (isPlaying) kotlin.random.Random.nextFloat() * 0.8f + 0.2f else 0.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 300 + kotlin.random.Random.nextInt(400),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$i"
        )
    }

    val color = MaterialTheme.colorScheme.onSurface
    Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
        val barWidth = size.width / (barCount * 2)
        val spacing = barWidth
        
        for (i in 0 until barCount) {
            val x = i * (barWidth + spacing) + spacing / 2
            val h = size.height * heights[i].value
            val y = (size.height - h) / 2
            
            drawLine(
                color = color,
                start = Offset(x, y),
                end = Offset(x, y + h),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}
