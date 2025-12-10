package com.krai29.karanbalakrishnaraitask.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.krai29.karanbalakrishnaraitask.presentation.viewmodel.PortfolioViewModel
import com.krai29.karanbalakrishnaraitask.ui.screens.StocksScaffold
import com.krai29.karanbalakrishnaraitask.ui.theme.KaranBalakrishnaRaiTaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StocksActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KaranBalakrishnaRaiTaskTheme {
                val viewModel: PortfolioViewModel = hiltViewModel()
                StocksScaffold(viewModel = viewModel)
            }
        }
    }
}