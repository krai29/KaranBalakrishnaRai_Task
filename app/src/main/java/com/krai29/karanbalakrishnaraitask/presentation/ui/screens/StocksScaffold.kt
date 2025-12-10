package com.krai29.karanbalakrishnaraitask.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.krai29.karanbalakrishnaraitask.presentation.model.HoldingUiModel
import com.krai29.karanbalakrishnaraitask.presentation.model.PortfolioUiState
import com.krai29.karanbalakrishnaraitask.presentation.viewmodel.PortfolioViewModel
import com.krai29.karanbalakrishnaraitask.ui.components.BottomNavItem
import com.krai29.karanbalakrishnaraitask.ui.components.BottomNavigationBar
import com.krai29.karanbalakrishnaraitask.ui.components.HoldingRow
import com.krai29.karanbalakrishnaraitask.ui.components.LoadingView
import com.krai29.karanbalakrishnaraitask.ui.components.PortfolioSummaryBottomSheet

@Composable
fun StocksScaffold(
    viewModel: PortfolioViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val holdings = viewModel.holdingsPaging.collectAsLazyPagingItems()

    // selected bottom nav item
    var selectedBottomItem by remember {
        mutableStateOf<BottomNavItem>(BottomNavItem.Portfolio)
    }
    // 0 = POSITIONS, 1 = HOLDINGS
    var selectedTabIndex by remember { mutableStateOf(1) }

    Scaffold(
        topBar = { PortfolioTopBar() },
        bottomBar = {
            BottomNavigationBar(
                selected = selectedBottomItem,
                onSelectedChange = { selectedBottomItem = it }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PortfolioTabs(
                selectedIndex = selectedTabIndex,
                onSelectedIndexChange = { selectedTabIndex = it }
            )

            when (selectedTabIndex) {
                0 -> PositionsPlaceholder()
                1 -> HoldingsTabContent(
                    uiState = uiState,
                    holdingsUi = holdings,
                    onRefresh = { viewModel.refresh() },
                    onToggleSummaryExpanded = { viewModel.toggleSummaryExpanded() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortfolioTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Portfolio",
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(28.dp)
            )
        },
        actions = {
            IconButton(onClick = { /* sorting not needed for task */ }) {
                Icon(
                    imageVector = Icons.Outlined.SwapVert,
                    contentDescription = "Sort",
                    tint = Color.White
                )
            }
            IconButton(onClick = { /* search not needed for task */ }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun PortfolioTabs(
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit
) {
    val titles = listOf("POSITIONS", "HOLDINGS")

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        titles.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onSelectedIndexChange(index) },
                text = {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = if (selectedIndex == index) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun PositionsPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Positions not implemented for this task")
    }
}

/**
 * Content of the HOLDINGS tab.
 */
@Composable
private fun HoldingsTabContent(
    uiState: PortfolioUiState,
    holdingsUi: LazyPagingItems<HoldingUiModel>,
    onRefresh: () -> Unit,
    onToggleSummaryExpanded: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                if (holdingsUi.itemCount == 0) {
                    // No data in DB
                    item {
                        val message = uiState.errorMessage
                            ?: if (uiState.isLoading) {
                                ""
                            } else {
                                "No holdings to display. Check your connection and try again."
                            }

                        if (message.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = message)
                                Spacer(modifier = Modifier.height(12.dp))
                                TextButton(onClick = onRefresh) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                } else {
                    // Normal list of holdings
                    items(
                        count = holdingsUi.itemCount,
                        key = { index -> holdingsUi[index]?.symbol ?: index }
                    ) { index ->
                        val item = holdingsUi[index]
                        if (item != null) {
                            HoldingRow(holding = item)
                            HorizontalDivider()
                        }
                    }
                }
            }

            PortfolioSummaryBottomSheet(
                uiState = uiState,
                onToggleExpanded = onToggleSummaryExpanded
            )
        }

        if (uiState.isLoading && holdingsUi.itemCount == 0) {
            LoadingView(modifier = Modifier.align(Alignment.Center))
        }
    }
}
