package com.krai29.karanbalakrishnaraitask.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krai29.karanbalakrishnaraitask.domain.model.PortfolioSummary
import com.krai29.karanbalakrishnaraitask.presentation.model.PortfolioUiState

@Composable
fun PortfolioSummaryBottomSheet(
    uiState: PortfolioUiState,
    onToggleExpanded: () -> Unit
) {
    val summary = uiState.summary ?: return

    val arrowRotation by animateFloatAsState(
        targetValue = if (uiState.isSummaryExpanded) 180f else 0f,
        label = "pnl_arrow_rotation"
    )

    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 12.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            AnimatedVisibility(visible = uiState.isSummaryExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SummarySheetRows(summary = summary)

                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onToggleExpanded() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profit & Loss*",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Toggle summary",
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .rotate(arrowRotation)
                )

                Spacer(modifier = Modifier.weight(1f))

                val pnlColor =
                    if (summary.totalPnl >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)

                Text(
                    text = "₹%.2f".format(summary.totalPnl),
                    color = pnlColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "(%.2f%%)".format(summary.totalPnlPercent),
                    color = pnlColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun SummarySheetRows(
    summary: PortfolioSummary
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp)
    ) {
        SummaryRow(
            label = "Current value*",
            value = "₹%.2f".format(summary.currentValue)
        )
        SummaryRow(
            label = "Total investment*",
            value = "₹%.2f".format(summary.totalInvestment)
        )
        val todaysColor =
            if (summary.todaysPnl >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
        SummaryRow(
            label = "Today’s Profit & Loss*",
            value = "₹%.2f".format(summary.todaysPnl),
            valueColor = todaysColor
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}
