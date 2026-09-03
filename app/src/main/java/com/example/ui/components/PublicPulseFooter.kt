package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.PublicPulseEngine
import com.example.ui.theme.GeistFamily
import com.example.ui.theme.GeistMonoFamily

@Composable
fun PublicPulseFooter(headline: String, url: String) {
    var expanded by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<PublicPulseEngine.PulseResult?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }

    LaunchedEffect(headline, url) {
        if (!hasSearched) {
            hasSearched = true
            isLoading = true
            result = PublicPulseEngine.analyzeArticle(headline, url)
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable { if (result != null) expanded = !expanded }
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Collapsed Pill View
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "ANALYZING PUBLIC PULSE...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = GeistMonoFamily,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    } else if (result == null) {
                        Text(
                            "No public chatter found about this story yet. You're ahead of the curve.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = GeistMonoFamily,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    } else {
                        val res = result!!
                        val color = when {
                            res.sentimentScore > 20 -> Color(0xFF4ADE80) // Green
                            res.sentimentScore < -20 -> Color(0xFFF87171) // Red
                            else -> Color(0xFFFBBF24) // Amber
                        }
                        
                        AnimatedVisibility(!expanded) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (res.sentimentScore > 0) "+${res.sentimentScore}" else "${res.sentimentScore}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontFamily = GeistFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = color
                                    )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "PUBLIC PULSE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = GeistMonoFamily,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                letterSpacing = 1.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "X: ${if (res.platformBreakdown["X"]!! > 0) "+" else ""}${res.platformBreakdown["X"]} • Reddit: ${if (res.platformBreakdown["Reddit"]!! > 0) "+" else ""}${res.platformBreakdown["Reddit"]}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = GeistMonoFamily,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                    Text(
                                        res.pulseSummary,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = GeistMonoFamily,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        
                        AnimatedVisibility(expanded) {
                            Text(
                                "PUBLIC PULSE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = GeistMonoFamily,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }
                }
                
                if (!isLoading && result != null) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Expanded View
            AnimatedVisibility(
                visible = expanded && result != null && !isLoading,
                enter = expandVertically(animationSpec = spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessLow)) + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                val res = result ?: return@AnimatedVisibility
                val color = when {
                    res.sentimentScore > 20 -> Color(0xFF4ADE80)
                    res.sentimentScore < -20 -> Color(0xFFF87171)
                    else -> Color(0xFFFBBF24)
                }
                        
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    // Massive Gooey Score
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (res.sentimentScore > 0) "+${res.sentimentScore}" else "${res.sentimentScore}",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontFamily = GeistFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 72.sp,
                                    color = color
                                )
                            )
                            Text(
                                res.overallSentiment.uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = GeistMonoFamily,
                                    color = color,
                                    letterSpacing = 2.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Breakdown Bar Chart (Micrographics)
                    Column {
                        Text(
                            "PLATFORM BREAKDOWN",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("X (Twitter)", style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily))
                            Text("${res.platformBreakdown["X"]}", style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily, color = if ((res.platformBreakdown["X"] ?: 0) > 0) Color(0xFF4ADE80) else Color(0xFFF87171)))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Reddit", style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily))
                            Text("${res.platformBreakdown["Reddit"]}", style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily, color = if ((res.platformBreakdown["Reddit"] ?: 0) > 0) Color(0xFF4ADE80) else Color(0xFFF87171)))
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        "TOP REASON",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = GeistMonoFamily,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        res.topReason,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = GeistFamily,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        "MOST REPRESENTATIVE COMMENTS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = GeistMonoFamily,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    res.representativeComments.forEach { comment ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.03f))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "${comment.platform} • @${comment.author}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = GeistMonoFamily,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                    val badgeColor = when (comment.sentiment.lowercase()) {
                                        "positive" -> Color(0xFF4ADE80)
                                        "negative" -> Color(0xFFF87171)
                                        else -> Color(0xFFFBBF24)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(badgeColor.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            comment.sentiment.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = GeistMonoFamily,
                                                color = badgeColor,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    comment.text,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = GeistFamily,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 15.sp,
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        "Automated sentiment analysis of public social media. Results may not represent general public opinion.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = GeistMonoFamily,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
