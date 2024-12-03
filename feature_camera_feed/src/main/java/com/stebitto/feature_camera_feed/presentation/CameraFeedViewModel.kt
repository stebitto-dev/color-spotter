package com.stebitto.feature_camera_feed.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stebitto.common.MVIViewModel
import com.stebitto.common.stateInWhileSubscribed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class CameraFeedViewModel(
    initialState: CameraFeedState = CameraFeedState()
) : ViewModel(), MVIViewModel<CameraFeedState, CameraFeedIntent, CameraFeedEffect> {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<CameraFeedState> = _state.stateInWhileSubscribed(initialState)

    private val _sideEffects = Channel<CameraFeedEffect>()
    override val sideEffects: Flow<CameraFeedEffect>
        get() = _sideEffects.receiveAsFlow()

    override fun dispatch(intent: CameraFeedIntent) {
        when (intent) {
            is CameraFeedIntent.OnFrameAnalyze -> viewModelScope.launch { onFrameAnalyze(intent.bitmap, intent.targetRadius) }
        }
    }

    private suspend fun onFrameAnalyze(bitmap: Bitmap, targetRadius: Float) = withContext(Dispatchers.IO) {
        val xCoordinate = bitmap.width / 2f
        val yCoordinate = bitmap.height / 2f

        val redsList = mutableListOf<Int>()
        val bluesList = mutableListOf<Int>()
        val greensList = mutableListOf<Int>()

        for (x in (xCoordinate - targetRadius).toInt() until (xCoordinate + targetRadius).toInt()) {
            for (y in (yCoordinate - targetRadius).toInt() until (yCoordinate + targetRadius).toInt()) {
                val point = bitmap.getPixel(x, y)
                if (isPixelInCircle(x.toFloat(), y.toFloat(), xCoordinate, yCoordinate, targetRadius)) {
                    val red = android.graphics.Color.red(point)
                    redsList.add(red)
                    val green = android.graphics.Color.green(point)
                    greensList.add(green)
                    val blue = android.graphics.Color.blue(point)
                    bluesList.add(blue)
                }
            }
        }

        val redAverage = redsList.average()
        val greenAverage = greensList.average()
        val blueAverage = bluesList.average()

        val color = Color(redAverage.toInt(), greenAverage.toInt(), blueAverage.toInt())
        Log.d("CameraPreview", "Color: $color")

        _state.update { state -> state.copy(colorRgb = color) }
    }
}

private fun isPixelInCircle(
    x: Float,
    y: Float,
    centerX: Float,
    centerY: Float,
    radius: Float
): Boolean {
    val dx = x - centerX
    val dy = y - centerY
    return dx * dx + dy * dy <= radius * radius
}