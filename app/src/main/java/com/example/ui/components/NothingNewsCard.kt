package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NewsArticle
import com.example.ui.screens.NewsMood

@Composable
fun NothingNewsCard(
    article: NewsArticle,
    index: Int,
    onArticleClick: (NewsArticle) -> Unit,
    onBookmarkToggle: (NewsArticle) -> Unit,
    onListen: (NewsArticle) -> Unit,
    onShare: (NewsArticle) -> Unit,
    modifier: Modifier = Modifier,
    mood: NewsMood = NewsMood.BALANCED,
    timeCompressionMinutes: Int? = null,
    compressedSummary: String? = null
) {
    var isExpanded by remember { mutableStateOf(false) }
    val isRead = article.readCount > 0
    val headlineColor = if (isRead) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
    val summaryColor = if (isRead) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurfaceVariant
    val isTimeCompressed = timeCompressionMinutes != null
    val isOneMinMode = timeCompressionMinutes == 1
    val isThirtyMinMode = timeCompressionMinutes == 30

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = NothingOsIntSizeSpring)
            .testTag("news_card_${article.id}"),
        onClick = { onArticleClick(article) }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Row: Source + Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = article.source.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        color = if (isRead) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (timeCompressionMinutes != null) {
                        val compLabel = when (timeCompressionMinutes) {
                            1 -> "1-MIN // 15w"
                            5 -> "5-MIN // 75w"
                            15 -> "15-MIN // 225w"
                            else -> "30-MIN // DEEP DIVE"
                        }
                        MonoTag(text = compLabel, isRed = true)
                    } else {
                        if (article.isBreaking) {
                            MonoTag(text = "BREAKING", isRed = true)
                        }
                        if (article.isPalestine) {
                            MonoTag(text = "PALESTINE", isRed = false)
                        }
                        MonoTag(text = article.category, isRed = false)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Hide image in 1-min mode for ultra-short punchy rendering
            if (article.imageUrl.isNotBlank() && mood != NewsMood.QUICK_SCAN && !isOneMinMode) {
                coil.compose.AsyncImage(
                    model = article.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop,
                    alpha = if (isRead) 0.6f else 1.0f
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Headline
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = if (isOneMinMode) FontWeight.ExtraBold else FontWeight.SemiBold,
                    fontSize = if (isOneMinMode) 18.sp else 17.sp,
                    lineHeight = 24.sp,
                    color = headlineColor
                )
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Summary
            val summaryMaxLines = when {
                isExpanded -> 16
                isOneMinMode -> 2
                timeCompressionMinutes == 5 -> 4
                timeCompressionMinutes == 15 -> 8
                isThirtyMinMode -> 16
                mood == NewsMood.QUICK_SCAN -> 2
                else -> 3
            }

            val rawSummary = if (isTimeCompressed) {
                val candidate = compressedSummary ?: article.summary
                // If 1-min mode has "**Headline** — ", strip it for clean punchy sentence rendering
                if (isOneMinMode) {
                    candidate.replace(Regex("^\\*\\*.*?\\*\\*\\s*—?\\s*"), "")
                } else {
                    candidate
                }
            } else {
                if (mood == NewsMood.QUICK_SCAN && !isExpanded) "⚡ AI SUMMARY: ${article.summary}" else article.summary
            }

            Text(
                text = rawSummary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = summaryColor,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                maxLines = summaryMaxLines
            )

            // Key takeaways preview (omitted in 1-min mode per prompt specs)
            if (article.keyTakeaways.isNotBlank() && mood != NewsMood.QUICK_SCAN && !isOneMinMode) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (isThirtyMinMode) {
                            "• BACKGROUND & CONTEXT: " + article.keyTakeaways.replace(", ", "\n• ").replace(" • ", "\n• ")
                        } else {
                            "• " + article.keyTakeaways.replace(", ", "\n• ").replace(" • ", "\n• ")
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            // In-place Quick Expand Preview
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = NothingOsIntSizeSpring),
                exit = shrinkVertically(animationSpec = NothingOsIntSizeSpring)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text(
                        text = "FULL BRIEFING:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = article.fullContent,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer info + Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = article.publishedAt,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "${article.location} • ${article.estimatedReadTimeMin} MIN READ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.UnfoldMore,
                            contentDescription = "Expand in place",
                            tint = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { onListen(article) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                            contentDescription = "Read aloud",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { onShare(article) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { onBookmarkToggle(article) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (article.isBookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (article.isBookmarked) "Saved" else "Save",
                            tint = if (article.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
