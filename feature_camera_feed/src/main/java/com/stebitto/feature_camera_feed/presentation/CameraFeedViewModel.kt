package com.stebitto.feature_camera_feed.presentation

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stebitto.common.MVIViewModel
import com.stebitto.common.data.ColorDTO
import com.stebitto.common.data.ColorRepository
import com.stebitto.common.stateInWhileSubscribed
import com.stebitto.feature_camera_feed.CAPTURE_ANALYSIS_INTERVAL
import com.stebitto.feature_camera_feed.data.GetTargetAreaColorUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

internal class CameraFeedViewModel(
    private val getTargetAreaColorUseCase: GetTargetAreaColorUseCase,
    private val colorRepository: ColorRepository,
    initialState: CameraFeedState = CameraFeedState()
) : ViewModel(), MVIViewModel<CameraFeedState, CameraFeedIntent, CameraFeedEffect> {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<CameraFeedState> = _state.stateInWhileSubscribed(initialState)

    private val _sideEffects = Channel<CameraFeedEffect>()
    override val sideEffects: Flow<CameraFeedEffect>
        get() = _sideEffects.receiveAsFlow()

    override fun dispatch(intent: CameraFeedIntent) {
        viewModelScope.launch {
            when (intent) {
                is CameraFeedIntent.OnFrameAnalyze -> onFrameAnalyze(intent.bitmap, intent.targetRadius)
                CameraFeedIntent.OnStartAnalysis -> onStartAnalysis()
                CameraFeedIntent.OnStopAnalysis -> onStopAnalysis()
                CameraFeedIntent.OnCameraNotReady -> _sideEffects.send(CameraFeedEffect.ShowToastCameraNotReady)
                CameraFeedIntent.OnGoToColorHistory -> onGoToColorHistory()
            }
        }
    }

    private var lastSeen = Date()
    private suspend fun onFrameAnalyze(bitmap: Bitmap, targetRadius: Float) {
        if (Date().time - lastSeen.time < CAPTURE_ANALYSIS_INTERVAL) return
        lastSeen = Date()

        getTargetAreaColorUseCase(bitmap, targetRadius)
            .onFailure {
                _state.update { state ->
                    state.copy(
                        colorInt = null,
                        colorName = "ERROR",
                        colorHex = "",
                        colorRed = -1,
                        colorGreen = -1,
                        colorBlue = -1,
                        colorLuminance = -1f
                    )
                }
            }
            .onSuccess { (color, colorName) ->
                colorRepository.insertColor(
                    ColorDTO(
                        name = colorName,
                        lastSeen = Date().time,
                        hexCode = Integer.toHexString(color).substring(2),
                        red = Color.red(color),
                        green = Color.green(color),
                        blue = Color.blue(color)
                    )
                )
                _state.update { state ->
                    state.copy(
                        colorInt = color,
                        colorName = colorName,
                        colorHex = Integer.toHexString(color).substring(2),
                        colorRed = Color.red(color),
                        colorGreen = Color.green(color),
                        colorBlue = Color.blue(color),
                        colorLuminance = Color.luminance(color)
                    )
                }
            }
    }

    private suspend fun onStartAnalysis() {
        _sideEffects.send(CameraFeedEffect.StartFrameAnalysis)
        _state.update { state -> state.copy(isAnalyzing = true) }
    }

    private suspend fun onStopAnalysis() {
        _sideEffects.send(CameraFeedEffect.StopFrameAnalysis)
        _state.update { state -> state.copy(isAnalyzing = false) }
    }

    private suspend fun onGoToColorHistory() {
        _sideEffects.send(CameraFeedEffect.GoToColorHistory)
        _state.update { state -> state.copy(isAnalyzing = false) }
    }
}