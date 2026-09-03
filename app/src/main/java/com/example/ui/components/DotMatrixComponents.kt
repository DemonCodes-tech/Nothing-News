package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NothingLightGray
import com.example.ui.theme.NothingSurfaceDark

@Composable
fun LivePulsingDot(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.tertiary,
    size: Dp = 8.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(650),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

@Composable
fun DotMatrixGridHeader(
    modifier: Modifier = Modifier,
    title: String = "NOTHING (NEWS)",
    subTitle: String = "GLOBAL DISPATCH // PALESTINE",
    liveCount: Int = 14,
    onRefresh: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LivePulsingDot(size = 10.dp)
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = com.example.ui.theme.NdotFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, Color.Transparent, RoundedCornerShape(50))
                    .background(NothingSurfaceDark)
                    .clickable { onRefresh() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("refresh_button")
            ) {
                Text(
                    text = "[ SYNC ]",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = subTitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            )

            Text(
                text = "DISPATCHES: $liveCount",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dot Matrix horizontal rule
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
        ) {
            val dotSpacing = 8.dp.toPx()
            val dotRadius = 1.dp.toPx()
            var currentX = 0f
            while (currentX < size.width) {
                drawCircle(
                    color = Color.Transparent,
                    radius = dotRadius,
                    center = Offset(currentX, size.height / 2)
                )
                currentX += dotSpacing
            }
        }
    }
}

@Composable
fun NothingPillTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int? = null,
    isRedAccent: Boolean = false
) {
    val borderColor = when {
        isSelected && isRedAccent -> MaterialTheme.colorScheme.tertiary
        isSelected -> MaterialTheme.colorScheme.onSurface
        else -> Color.Transparent
    }

    val backgroundColor = when {
        isSelected && isRedAccent -> MaterialTheme.colorScheme.tertiaryContainer
        isSelected -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f)
    }

    val textColor = when {
        isSelected && isRedAccent -> MaterialTheme.colorScheme.onSurface
        isSelected -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("tab_${text.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (isRedAccent) {
                LivePulsingDot(size = 6.dp)
            }
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = textColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    letterSpacing = 1.sp
                )
            )
            if (badgeCount != null && badgeCount > 0) {
                Text(
                    text = "($badgeCount)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isSelected) textColor else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun NothingTag(
    text: String,
    modifier: Modifier = Modifier,
    isRed: Boolean = false
) {
    val bg = if (isRed) MaterialTheme.colorScheme.tertiaryContainer else NothingSurfaceDark
    val border = if (isRed) MaterialTheme.colorScheme.tertiary else Color.Transparent
    val textColor = if (isRed) MaterialTheme.colorScheme.tertiary else NothingLightGray

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, border, RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
