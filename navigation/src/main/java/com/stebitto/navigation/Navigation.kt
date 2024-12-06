package com.stebitto.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.stebitto.feature_camera_feed.presentation.CameraFeedScreen
import com.stebitto.feature_color_history.presentation.AcquiredColorScreen

fun NavGraphBuilder.cameraFeedNavGraph(
    onNavigateToColorHistory: () -> Unit
) {
    composable(CameraFeedRoutes.CAMERA_FEED.name) {
        CameraFeedScreen(onNavigateToColorHistory = onNavigateToColorHistory)
    }
}

fun NavGraphBuilder.colorHistoryNavGraph(
    onNavigateBack: () -> Unit
) {
    composable(ColorHistoryRoutes.COLOR_HISTORY.name) {
        AcquiredColorScreen(onNavigateBack = onNavigateBack)
    }
}