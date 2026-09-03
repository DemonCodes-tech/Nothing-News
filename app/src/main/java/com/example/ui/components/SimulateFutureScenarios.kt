package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.GeminiForecaster
import kotlinx.coroutines.launch

@Composable
fun SimulateFutureScenarios(articleText: String, modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var scenarios by remember { mutableStateOf<List<GeminiForecaster.ForecastScenario>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxWidth()) {
        // Expand Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NothingBorderDark)
                .background(if (isExpanded) NothingSurfaceDark else NothingBlack)
                .clickable {
                    isExpanded = !isExpanded
                    if (isExpanded && scenarios == null && !isLoading) {
                        // Trigger AI fetch
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            val results = GeminiForecaster.generateScenarios(articleText)
                            if (results != null && results.isNotEmpty()) {
                                scenarios = results
                            } else {
                                errorMessage = "Failed to generate scenarios. Ensure GEMINI_API_KEY is configured in Secrets."
                            }
                            isLoading = false
                        }
                    }
                }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = if (isExpanded) NothingWhite else NothingLightGray,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isExpanded) "[ CLOSE SIMULATION ]" else "[ SIMULATE FUTURE OUTCOMES ]",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = NdotFontFamily,
                        color = if (isExpanded) NothingWhite else NothingLightGray,
                        letterSpacing = 1.sp
                    )
                )
            }
        }

        // Expanded Content
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(300)),
            exit = shrinkVertically(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NothingBorderDark)
                    .background(NothingBlack)
                    .padding(16.dp)
            ) {
                if (isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = NothingWhite,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "RUNNING PREDICTIVE MODELS...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = NdotFontFamily,
                                color = NothingGray,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NothingRed
                        )
                    )
                } else if (scenarios != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        scenarios!!.forEach { scenario ->
                            ScenarioCard(scenario)
                        }
                        
                        Text(
                            text = "GENERATED BY GEMINI 1.5 FLASH",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = NdotFontFamily,
                                color = NothingBorderDark,
                                fontSize = 8.sp,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScenarioCard(scenario: GeminiForecaster.ForecastScenario) {
    val icon = when {
        scenario.type.contains("BEST", ignoreCase = true) -> Icons.Outlined.CheckCircle
        scenario.type.contains("WORST", ignoreCase = true) -> Icons.Filled.Warning
        else -> Icons.Outlined.Analytics
    }
    val color = when {
        scenario.type.contains("BEST", ignoreCase = true) -> NothingWhite
        scenario.type.contains("WORST", ignoreCase = true) -> NothingRed
        else -> NothingLightGray
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "${scenario.type.uppercase()} // ${scenario.title.uppercase()}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = NdotFontFamily,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = scenario.description,
            style = MaterialTheme.typography.bodySmall.copy(
                color = NothingOffWhite,
                lineHeight = 18.sp
            )
        )
    }
}
