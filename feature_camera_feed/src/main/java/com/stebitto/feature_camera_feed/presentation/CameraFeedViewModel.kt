package com.stebitto.feature_camera_feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stebitto.common.MVIViewModel
import com.stebitto.common.data.ColorDTO
import com.stebitto.common.data.ColorRepository
import com.stebitto.common.stateInWhileSubscribed
import com.stebitto.feature_camera_feed.CAPTURE_ANALYSIS_INTERVAL
import com.stebitto.feature_camera_feed.models.BitmapWrapper
import com.stebitto.feature_camera_feed.data.GetTargetAreaColorUseCase
import com.stebitto.feature_camera_feed.data.TimerIntervalUseCase
import com.stebitto.feature_camera_feed.models.ColorPresentationModel
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
    private val timerIntervalUseCase: TimerIntervalUseCase,
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
                is CameraFeedIntent.OnFrameAnalyze -> onFrameAnalyze(intent.bitmapWrapper, intent.targetRadius)
                CameraFeedIntent.OnStartAnalysis -> onStartAnalysis()
                CameraFeedIntent.OnStopAnalysis -> onStopAnalysis()
                CameraFeedIntent.OnCameraNotReady -> _sideEffects.send(CameraFeedEffect.ShowToastCameraNotReady)
                CameraFeedIntent.OnGoToColorHistory -> onGoToColorHistory()
            }
        }
    }

    private suspend fun onFrameAnalyze(bitmap: BitmapWrapper, targetRadius: Float) {
        if (!timerIntervalUseCase(CAPTURE_ANALYSIS_INTERVAL)) return

        getTargetAreaColorUseCase(bitmap, targetRadius)
            .onFailure {
                _state.update { state ->
                    state.copy(
                        colorPresentationModel = ColorPresentationModel(
                            colorInt = null,
                            colorName = "ERROR"
                        )
                    )
                }
            }
            .onSuccess { (color, colorName) ->
                colorRepository.insertColor(
                    ColorDTO(
                        name = colorName,
                        lastSeen = Date().time,
                        colorInt = color
                    )
                )
                _state.update { state ->
                    state.copy(
                        colorPresentationModel = ColorPresentationModel(
                            colorInt = color,
                            colorName = colorName
                        )
                    )
                }
            }
    }

    private fun onStartAnalysis() {
        _sideEffects.trySend(CameraFeedEffect.StartFrameAnalysis)
        _state.update { state -> state.copy(isAnalyzing = true) }
    }

    private fun onStopAnalysis() {
        _sideEffects.trySend(CameraFeedEffect.StopFrameAnalysis)
        _state.update { state -> state.copy(isAnalyzing = false) }
    }

    private fun onGoToColorHistory() {
        _sideEffects.trySend(CameraFeedEffect.GoToColorHistory)
        _state.update { state -> state.copy(isAnalyzing = false) }
    }
}