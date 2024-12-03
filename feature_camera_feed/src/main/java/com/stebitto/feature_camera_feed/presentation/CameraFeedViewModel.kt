package com.stebitto.feature_camera_feed.presentation

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stebitto.common.MVIViewModel
import com.stebitto.common.stateInWhileSubscribed
import com.stebitto.feature_camera_feed.CAPTURE_ANALYSIS_INTERVAL
import com.stebitto.feature_camera_feed.data.GetTargetAreaColorUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

internal class CameraFeedViewModel(
    private val getTargetAreaColorUseCase: GetTargetAreaColorUseCase,
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
            }
        }
    }

    private var lastSeen = Date()
    private suspend fun onFrameAnalyze(bitmap: Bitmap, targetRadius: Float) {
        if (Date().time - lastSeen.time < CAPTURE_ANALYSIS_INTERVAL) return
        lastSeen = Date()

        getTargetAreaColorUseCase(bitmap, targetRadius)
            .onFailure { _state.update { state -> state.copy(colorName = "ERROR", colorRgb = Color.Red) } }
            .onSuccess { (color, colorName) ->
                _state.update { state -> state.copy(colorName = colorName, colorRgb = color) }
            }
    }
}