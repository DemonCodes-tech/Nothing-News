package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NewsArticle
import com.example.ui.theme.GeistFamily
import com.example.ui.theme.GeistMonoFamily
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun SwipeSortGame(
    articles: List<NewsArticle>,
    onDismiss: () -> Unit,
    onBookmark: (NewsArticle) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val currentArticle = articles.getOrNull(currentIndex)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xEB000000)) // Deep frosted black
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Close Button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
        }
        
        // Mode Title
        Text(
            text = "SWIPE // RAPID TRIAGE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = GeistMonoFamily,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 12.dp)
        )

        if (currentArticle == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ALL CAUGHT UP",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = GeistMonoFamily,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("RETURN TO FEED", style = MaterialTheme.typography.labelMedium.copy(fontFamily = GeistMonoFamily))
                }
            }
        } else {
            // Render the next card beneath (if exists) for depth
            val nextArticle = articles.getOrNull(currentIndex + 1)
            if (nextArticle != null) {
                SwipeCard(
                    article = nextArticle,
                    modifier = Modifier.graphicsLayer {
                        scaleX = 0.95f
                        scaleY = 0.95f
                        translationY = 40f
                    }
                )
            }

            // Render current active card
            ActiveSwipeCard(
                article = currentArticle,
                onSwipedRight = {
                    onBookmark(currentArticle)
                    currentIndex++
                },
                onSwipedLeft = {
                    currentIndex++ // Skip
                }
            )
            
            // HUD at bottom
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Dislike
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.6f)).border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Close, contentDescription = "Skip", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("SKIP", style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily, color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
                // Like
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primary.copy(alpha=0.2f)).border(1.dp, MaterialTheme.colorScheme.primary.copy(0.5f), RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Bookmark, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("SAVE", style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMonoFamily, color = MaterialTheme.colorScheme.primary))
                }
            }
        }
    }
}

@Composable
fun ActiveSwipeCard(
    article: NewsArticle,
    onSwipedRight: () -> Unit,
    onSwipedLeft: () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    
    val swipeThreshold = 300f

    SwipeCard(
        article = article,
        modifier = Modifier
            .offset(x = offsetX.value.dp, y = offsetY.value.dp)
            .graphicsLayer {
                rotationZ = offsetX.value / 20f
                alpha = 1f - (abs(offsetX.value) / 1000f) // slight fade
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        if (offsetX.value > swipeThreshold) {
                            scope.launch {
                                offsetX.animateTo(1000f)
                                onSwipedRight()
                            }
                        } else if (offsetX.value < -swipeThreshold) {
                            scope.launch {
                                offsetX.animateTo(-1000f)
                                onSwipedLeft()
                            }
                        } else {
                            scope.launch {
                                offsetX.animateTo(0f, spring(dampingRatio = 0.8f, stiffness = 120f))
                            }
                            scope.launch {
                                offsetY.animateTo(0f, spring(dampingRatio = 0.8f, stiffness = 120f))
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x * 0.5f)
                            offsetY.snapTo(offsetY.value + dragAmount.y * 0.5f)
                        }
                    }
                )
            }
    ) {
        // Overlay for feedback
        AnimatedVisibility(visible = offsetX.value > 100f, enter = fadeIn(), exit = fadeOut()) {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary.copy(alpha=0.3f))) {
                Text("SAVE", style = MaterialTheme.typography.displayLarge.copy(fontFamily = GeistMonoFamily, color = Color.White, fontWeight = FontWeight.Black), modifier = Modifier.align(Alignment.Center).graphicsLayer { rotationZ = -15f })
            }
        }
        AnimatedVisibility(visible = offsetX.value < -100f, enter = fadeIn(), exit = fadeOut()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha=0.5f))) {
                Text("SKIP", style = MaterialTheme.typography.displayLarge.copy(fontFamily = GeistMonoFamily, color = Color.White, fontWeight = FontWeight.Black), modifier = Modifier.align(Alignment.Center).graphicsLayer { rotationZ = 15f })
            }
        }
    }
}

@Composable
fun SwipeCard(
    article: NewsArticle,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(500.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(2.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (article.imageUrl.isNotBlank()) {
                coil.compose.AsyncImage(
                    model = article.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(240.dp).clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(MaterialTheme.colorScheme.surfaceVariant))
            }
            Column(modifier = Modifier.padding(20.dp)) {
                MonoTag(text = article.source, isRed = article.isBreaking)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = GeistFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 3
                )
            }
        }
        overlay()
    }
}
