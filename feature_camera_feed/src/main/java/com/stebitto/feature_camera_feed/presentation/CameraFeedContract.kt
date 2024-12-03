package com.stebitto.feature_camera_feed.presentation

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import com.stebitto.common.Effect
import com.stebitto.common.Intent
import com.stebitto.common.State

internal data class CameraFeedState(
    val hasCameraPermission: Boolean = false,
    val colorName: String = "",
    val colorRgb: Color = Color.Gray
) : State

internal sealed class CameraFeedIntent : Intent {
    data class OnFrameAnalyze(val bitmap: Bitmap, val targetRadius: Float) : CameraFeedIntent()
}

internal sealed class CameraFeedEffect : Effect { }