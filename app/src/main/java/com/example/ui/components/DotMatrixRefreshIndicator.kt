package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DotMatrixRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFE53935) // Nothing OS Red
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    // Rotation for the refreshing state
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Opacity based on drag progress or refreshing state
    val alpha = if (isRefreshing) 1f else (state.distanceFraction * 1.5f).coerceIn(0f, 1f)
    
    // Scale based on drag progress or refreshing state
    val scale = if (isRefreshing) 1f else state.distanceFraction.coerceIn(0f, 1f)

    if (alpha > 0f) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(24.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2
                val dotCount = 8
                
                // When dragging, it rotates based on distance. When refreshing, it animates.
                val baseAngle = if (isRefreshing) rotation else (state.distanceFraction * 360f)

                for (i in 0 until dotCount) {
                    val angleOffset = i * (360f / dotCount)
                    val currentAngle = (baseAngle + angleOffset) % 360f
                    val angleRad = Math.toRadians(currentAngle.toDouble())
                    
                    val x = center.x + radius * cos(angleRad).toFloat()
                    val y = center.y + radius * sin(angleRad).toFloat()
                    
                    // Dot size varies to create a "tail" effect during rotation
                    // or just a solid dot-matrix circle
                    val dotRadius = if (isRefreshing) {
                        // Create a tail effect where one dot is biggest and it shrinks around the circle
                        val sizeMultiplier = (currentAngle / 360f)
                        2.dp.toPx() * (0.3f + 0.7f * sizeMultiplier)
                    } else {
                        2.dp.toPx() * scale
                    }

                    drawCircle(
                        color = color.copy(alpha = alpha * if (isRefreshing) (0.3f + 0.7f * (currentAngle / 360f)) else 1f),
                        radius = dotRadius,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}
