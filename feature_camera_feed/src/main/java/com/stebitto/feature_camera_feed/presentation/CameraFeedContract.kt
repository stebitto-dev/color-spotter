package com.stebitto.feature_camera_feed.presentation

import android.graphics.Bitmap
import com.stebitto.common.Effect
import com.stebitto.common.Intent
import com.stebitto.common.State

internal data class CameraFeedState(
    val isAnalyzing: Boolean = false,
    val colorInt: Int? = null,
    val colorName: String = "",
    val colorHex: String = "",
    val colorRed: Int = -1,
    val colorGreen: Int = -1,
    val colorBlue: Int = -1,
    val colorLuminance: Float = -1f
) : State

internal sealed class CameraFeedIntent : Intent {
    data object OnStartAnalysis : CameraFeedIntent()
    data object OnStopAnalysis : CameraFeedIntent()
    data class OnFrameAnalyze(val bitmap: Bitmap, val targetRadius: Float) : CameraFeedIntent()
    data object OnCameraNotReady : CameraFeedIntent()
    data object OnGoToColorHistory : CameraFeedIntent()
}

internal sealed class CameraFeedEffect : Effect {
    data object StartFrameAnalysis : CameraFeedEffect()
    data object StopFrameAnalysis : CameraFeedEffect()
    data object ShowToastCameraNotReady : CameraFeedEffect()
    data object GoToColorHistory : CameraFeedEffect()
}