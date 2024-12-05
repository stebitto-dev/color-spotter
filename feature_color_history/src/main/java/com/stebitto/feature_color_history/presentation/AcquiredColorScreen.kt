package com.stebitto.feature_color_history.presentation

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel

@Composable
fun AcquiredColorScreen(
    onNavigateBack: () -> Unit
) {
    val colorHistoryViewModel = koinViewModel<ColorHistoryViewModel>()

    ColorHistoryScreen(
        viewModel = colorHistoryViewModel,
        onNavigateBack = onNavigateBack
    )
}