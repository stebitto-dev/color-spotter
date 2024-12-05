package com.stebitto.feature_color_history.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun ColorHistoryScreen() {
    val colorHistoryViewModel = koinViewModel<ColorHistoryViewModel>()

    Scaffold { padding ->
        ColorHistory(
            modifier = Modifier.padding(padding),
            viewModel = colorHistoryViewModel
        )
    }
}