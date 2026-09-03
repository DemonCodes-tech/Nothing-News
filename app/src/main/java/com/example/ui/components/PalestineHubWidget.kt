package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorderDark
import com.example.ui.theme.NothingGray
import com.example.ui.theme.NothingLightGray
import com.example.ui.theme.NothingOffWhite
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingRedDim
import com.example.ui.theme.NothingSurfaceDark
import com.example.ui.theme.NothingWhite

@Composable
fun PalestineHumanitarianMonitorWidget(
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            
            
            .background(NothingSurfaceDark)
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Widget Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LivePulsingDot(color = NothingRed, size = 8.dp)
                    Text(
                        text = "PALESTINE CRISIS MONITOR",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = com.example.ui.theme.NdotFontFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = NothingWhite
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isExpanded) "[ LESS ]" else "[ EXPAND ]",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NothingGray,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = NothingGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4 Grid Stats in Nothing dot-matrix style
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricPill(
                    label = "HOSPITALS ACTIVE",
                    value = "14 / 36",
                    subtext = "Critical fuel alerts",
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    label = "DISPLACED POP.",
                    value = "1.9M",
                    subtext = "~90% of population",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricPill(
                    label = "WATER OUTPUT",
                    value = "-70%",
                    subtext = "Desalination deficit",
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    label = "WEST BANK ACCESS",
                    value = "6 HR AVG",
                    subtext = "Checkpoint delays",
                    modifier = Modifier.weight(1f)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    // Legal & Humanitarian Context
                    Text(
                        text = "INTERNATIONAL LEGAL RECORD & KEY BASES:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = com.example.ui.theme.NdotFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = NothingGray,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LegalPointItem(
                        tag = "ICJ ADVISORY",
                        desc = "The ICJ reaffirmed non-acquisition of territory by force and state duties regarding the Occupied Palestinian Territory."
                    )

                    LegalPointItem(
                        tag = "4TH GENEVA",
                        desc = "Full civilian protection, healthcare immunity, and prohibition of population transfers under Article 49."
                    )

                    LegalPointItem(
                        tag = "UN OCHA & WHO",
                        desc = "Ongoing verified situation tracking for humanitarian convoys, pediatric care, and utility restoration."
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(NothingBlack)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "SOURCE DATA: UN OCHA Flash Updates, WHO Health Cluster, UNRWA Daily Situation Memos.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NothingGray,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricPill(
    label: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, NothingBorderDark, RoundedCornerShape(6.dp))
            .background(NothingBlack)
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = com.example.ui.theme.NdotFontFamily,
                    color = NothingGray,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = com.example.ui.theme.NdotFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NothingWhite
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = NothingGray,
                    fontSize = 9.sp
                )
            )
        }
    }
}

@Composable
fun LegalPointItem(
    tag: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        NothingTag(text = tag, isRed = false)
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall.copy(
                color = NothingOffWhite,
                fontSize = 11.sp,
                lineHeight = 16.sp
            ),
            modifier = Modifier.weight(1f)
        )
    }
}
