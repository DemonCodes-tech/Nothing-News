package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.util.GeminiFactChecker
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NewsArticle
import com.example.ui.theme.GeistFamily
import com.example.ui.theme.GeistMonoFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinimalistReaderSheet(
    article: NewsArticle?,
    sheetState: SheetState,
    isPlayingAudio: Boolean,
    relatedMemories: List<com.example.ui.screens.RelatedMemory> = emptyList(),
    onDismiss: () -> Unit,
    onBookmarkToggle: (NewsArticle) -> Unit,
    onToggleAudio: (NewsArticle) -> Unit
) {
    if (article == null) return

    val context = LocalContext.current
    var fontSizeMultiplier by remember { mutableFloatStateOf(1.0f) }
    var useMonospace by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var factCheckResult by remember { mutableStateOf<GeminiFactChecker.FactCheckResult?>(null) }
    var isFactChecking by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent, // Let our own content draw the frosted glass
        dragHandle = null,
        modifier = Modifier
            .fillMaxHeight(0.96f)
            .testTag("reader_sheet")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xEB000000)) // Base frosted glass look
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .verticalScroll(scrollState)
            ) {
                // Hero Image with Depth Effect
                if (article.imageUrl.isNotBlank()) {
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                        coil.compose.AsyncImage(
                            model = article.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Gradient Overlay blending into the background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0x88000000),
                                            Color(0xEB000000)
                                        ),
                                        startY = 100f
                                    )
                                )
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(32.dp))
                }

                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    // Title
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontFamily = if (useMonospace) GeistMonoFamily else GeistFamily,
                            fontSize = (26 * fontSizeMultiplier).sp,
                            lineHeight = (32 * fontSizeMultiplier).sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Meta / Gooey Clock Style Date
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MonoTag(text = article.category, isRed = article.isBreaking)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = article.publishedAt.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = GeistMonoFamily,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Reading Controls Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Action Tools
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IconButton(onClick = { onToggleAudio(article) }, modifier = Modifier.size(28.dp)) {
                                Icon(
                                    imageVector = if (isPlayingAudio) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                    contentDescription = "Audio",
                                    tint = if (isPlayingAudio) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground
                                )
                            }
                            IconButton(onClick = { onBookmarkToggle(article) }, modifier = Modifier.size(28.dp)) {
                                Icon(
                                    imageVector = if (article.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (article.isBookmarked) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TITLE, article.title)
                                    putExtra(Intent.EXTRA_TEXT, "${article.title}\n\nVia Nothing News")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share"))
                            }, modifier = Modifier.size(28.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = "Share",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        FactCheckButton(onClick = {
                            if (!isFactChecking && factCheckResult == null) {
                                isFactChecking = true
                                coroutineScope.launch {
                                    factCheckResult = GeminiFactChecker.analyzeArticle(article.fullContent)
                                    isFactChecking = false
                                }
                            } else if (factCheckResult != null) {
                                factCheckResult = null // toggle off
                            }
                        })
                    }
                    
                    if (isFactChecking) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material3.CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "CROSS-CHECKING SOURCES...",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = GeistMonoFamily,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    } else if (factCheckResult != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    MonoTag(text = "AI FACT CHECK", isRed = true)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Text("FACTUAL CLAIMS", style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily, color = MaterialTheme.colorScheme.primary))
                                Spacer(modifier = Modifier.height(6.dp))
                                factCheckResult!!.claims.forEach { claim ->
                                    Text("• $claim", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("POTENTIAL BIAS", style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily, color = MaterialTheme.colorScheme.tertiary))
                                Spacer(modifier = Modifier.height(6.dp))
                                factCheckResult!!.biases.forEach { bias ->
                                    Text("• $bias", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("VERDICT", style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily, color = MaterialTheme.colorScheme.primary))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(factCheckResult!!.verdict, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Full Text Content
                    Text(
                        text = article.fullContent,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = if (useMonospace) GeistMonoFamily else GeistFamily,
                            fontSize = (16 * fontSizeMultiplier).sp,
                            lineHeight = (26 * fontSizeMultiplier).sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    SimulateFutureScenarios(articleText = article.fullContent)
                    if (relatedMemories.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "LOCAL KNOWLEDGE GRAPH // SYNTHESIS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = GeistMonoFamily,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        relatedMemories.forEach { memory ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "PAST READ: ${memory.articleTitle.uppercase()}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = GeistMonoFamily,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = memory.fact,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = GeistFamily,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    PublicPulseFooter(
                        headline = article.title,
                        url = article.url
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    TimeMachineFooter()

                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
            
            // Close button floating at the top right
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}
