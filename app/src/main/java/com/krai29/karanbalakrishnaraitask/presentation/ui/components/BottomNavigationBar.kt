package com.krai29.karanbalakrishnaraitask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.PriceChange
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class BottomNavItem(
    val label: String,
    val icon: ImageVector
) {
    object Watchlist : BottomNavItem("Watchlist", Icons.AutoMirrored.Outlined.ListAlt)
    object Orders : BottomNavItem("Orders", Icons.Outlined.WorkspacePremium)
    object Portfolio : BottomNavItem("Portfolio", Icons.Outlined.Wallet)
    object Funds : BottomNavItem("Funds", Icons.Outlined.PriceChange)
    object Invest : BottomNavItem("Invest", Icons.AutoMirrored.Outlined.TrendingUp)
}

@Composable
fun BottomNavigationBar(
    selected: BottomNavItem,
    onSelectedChange: (BottomNavItem) -> Unit
) {
    val items = listOf(
        BottomNavItem.Watchlist,
        BottomNavItem.Orders,
        BottomNavItem.Portfolio,
        BottomNavItem.Funds,
        BottomNavItem.Invest
    )

    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = Color(0xFF757575)
    val navBackground = Color(0xFFE3E3E3)

    NavigationBar(
        containerColor = navBackground
    ) {
        items.forEach { item ->
            val isSelected = selected == item

            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectedChange(item) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor,
                    indicatorColor = Color.Transparent
                ),
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(
                                    if (isSelected) selectedColor else Color.Transparent
                                )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    }
                },
                label = {
                    Text(text = item.label, fontSize = 11.sp)
                }
            )
        }
    }
}
