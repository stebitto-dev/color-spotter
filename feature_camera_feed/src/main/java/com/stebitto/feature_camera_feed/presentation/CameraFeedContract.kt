package com.stebitto.feature_camera_feed.presentation

import com.stebitto.common.Effect
import com.stebitto.common.Intent
import com.stebitto.common.State
import com.stebitto.feature_camera_feed.models.BitmapWrapper
import com.stebitto.feature_camera_feed.models.ColorPresentationModel

internal data class CameraFeedState(
    val isAnalyzing: Boolean = false,
    val colorPresentationModel: ColorPresentationModel = ColorPresentationModel()
) : State

internal sealed class CameraFeedIntent : Intent {
    data object OnStartAnalysis : CameraFeedIntent()
    data object OnStopAnalysis : CameraFeedIntent()
    data class OnFrameAnalyze(val bitmapWrapper: BitmapWrapper, val targetRadius: Float) : CameraFeedIntent()
    data object OnCameraNotReady : CameraFeedIntent()
    data object OnGoToColorHistory : CameraFeedIntent()
}

internal sealed class CameraFeedEffect : Effect {
    data object StartFrameAnalysis : CameraFeedEffect()
    data object StopFrameAnalysis : CameraFeedEffect()
    data object ShowToastCameraNotReady : CameraFeedEffect()
    data object GoToColorHistory : CameraFeedEffect()
}