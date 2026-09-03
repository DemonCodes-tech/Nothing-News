package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A subtle, constant horizontal scanning line animation across the screen
 * that evokes the retro-minimalist terminal / CRT phosphor aesthetic.
 * All touch events pass through without interference.
 */
@Composable
fun RetroScanlineOverlay(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    lineColor: Color = MaterialTheme.colorScheme.onBackground,
    durationMillis: Int = 2400
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.fadeIn(animationSpec = tween(300)),
        exit = androidx.compose.animation.fadeOut(animationSpec = tween(500)),
        modifier = modifier.fillMaxSize()
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "scanline_transition")
        val scanProgress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "scan_progress"
        )

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            if (width <= 0f || height <= 0f) return@Canvas

            val currentY = scanProgress * height
            val beamSpread = 42.dp.toPx()

            val startY = (currentY - beamSpread).coerceAtLeast(0f)
            val endY = (currentY + beamSpread).coerceAtMost(height)

            // Soft luminous terminal phosphor beam
            if (endY > startY) {
                val beamBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        lineColor.copy(alpha = 0.04f),
                        lineColor.copy(alpha = 0.12f),
                        lineColor.copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    startY = currentY - beamSpread,
                    endY = currentY + beamSpread
                )

                drawRect(
                    brush = beamBrush,
                    topLeft = Offset(0f, startY),
                    size = Size(width, endY - startY)
                )
            }

            // Crisp retro scanning line beam core
            drawLine(
                color = lineColor.copy(alpha = 0.28f),
                start = Offset(0f, currentY),
                end = Offset(width, currentY),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}
