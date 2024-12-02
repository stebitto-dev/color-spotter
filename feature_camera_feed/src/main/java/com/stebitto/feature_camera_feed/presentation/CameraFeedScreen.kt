package com.stebitto.feature_camera_feed.presentation

import android.Manifest
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraFeedScreen() {
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)

    if (permissionState.hasPermission) {
        CameraPreview()
    } else {
        CameraPermission { permissionState.launchPermissionRequest() }
    }
}