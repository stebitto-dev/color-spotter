package com.stebitto.feature_camera_feed.presentation

import android.Manifest
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraFeedScreen() {
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val cameraFeedViewModel = koinViewModel<CameraFeedViewModel>()

    if (permissionState.hasPermission) {
        CameraPreview(viewModel = cameraFeedViewModel)
    } else {
        CameraPermission { permissionState.launchPermissionRequest() }
    }
}