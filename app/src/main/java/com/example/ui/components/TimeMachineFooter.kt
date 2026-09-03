package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimeMachineFooter(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text(
                text = "TIME MACHINE // HISTORICAL CONTEXT",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = com.example.ui.theme.GeistMonoFamily,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TimeMachineCard(
                year = "1 YR AGO",
                event = "Similar minor conflicts reported in the region with minimal economic fallout."
            )
            TimeMachineCard(
                year = "5 YRS AGO",
                event = "Major treaty signed that temporarily stabilized the market. Current events threaten this."
            )
            TimeMachineCard(
                year = "10 YRS AGO",
                event = "Market crash caused by identical supply chain disruptions. High risk of repetition."
            )
        }
    }
}

@Composable
private fun TimeMachineCard(year: String, event: String) {
    GlassCard(modifier = Modifier.width(220.dp)) {
        Column {
            MonoTag(text = year, isRed = true)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            )
        }
    }
}
