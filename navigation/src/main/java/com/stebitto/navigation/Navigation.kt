package com.stebitto.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.stebitto.feature_camera_feed.presentation.CameraFeedScreen

fun NavGraphBuilder.cameraFeedNavGraph() {
    composable(CameraFeedRoutes.CAMERA_FEED.name) {
        CameraFeedScreen()
    }
}