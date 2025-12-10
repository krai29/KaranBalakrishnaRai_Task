package com.krai29.karanbalakrishnaraitask.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krai29.karanbalakrishnaraitask.presentation.model.HoldingUiModel

@Composable
fun HoldingRow(
    holding: HoldingUiModel,
    modifier: Modifier = Modifier
) {
    val labelColor = Color(0xFF9E9E9E) 
    val profitColor = Color(0xFF2E7D32)
    val lossColor = Color(0xFFC62828)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // First line: symbol and LTP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = holding.symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "LTP: ",
                    style = MaterialTheme.typography.bodySmall,
                    color = labelColor
                )
                Text(
                    text = holding.ltp,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Second line: NET QTY and P&L
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NET QTY: ${holding.quantity}",
                style = MaterialTheme.typography.bodySmall,
                color = labelColor
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "P&L: ",
                    style = MaterialTheme.typography.bodySmall,
                    color = labelColor
                )
                val valueColor = if (holding.pnlPositive) profitColor else lossColor
                Text(
                    text = holding.pnl,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = valueColor
                )
            }
        }
    }
}
