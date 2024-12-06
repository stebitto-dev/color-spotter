package com.stebitto.feature_camera_feed

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.stebitto.common.MainDispatcherRule
import com.stebitto.common.data.ColorRepository
import com.stebitto.feature_camera_feed.data.GetTargetAreaColorUseCase
import com.stebitto.feature_camera_feed.data.TimerIntervalUseCase
import com.stebitto.feature_camera_feed.models.BitmapWrapper
import com.stebitto.feature_camera_feed.models.ColorPresentationModel
import com.stebitto.feature_camera_feed.presentation.CameraFeedEffect
import com.stebitto.feature_camera_feed.presentation.CameraFeedIntent
import com.stebitto.feature_camera_feed.presentation.CameraFeedState
import com.stebitto.feature_camera_feed.presentation.CameraFeedViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class CameraFeedViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var getTargetAreaColorUseCase: GetTargetAreaColorUseCase

    @Mock
    private lateinit var timerIntervalUseCase: TimerIntervalUseCase

    @Mock
    private lateinit var colorRepository: ColorRepository

    private lateinit var viewModel: CameraFeedViewModel

    private val initialState = CameraFeedState()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = CameraFeedViewModel(getTargetAreaColorUseCase, timerIntervalUseCase, colorRepository)
    }

    @Test
    fun `test initial state`() {
        viewModel = CameraFeedViewModel(getTargetAreaColorUseCase, timerIntervalUseCase, colorRepository, initialState)
        assertEquals(viewModel.state.value, initialState)
    }

    @Test
    fun `on dispatch OnFrameAnalyze intent, update state with color presentation model`() = runTest {
        val bitmapWrapper = BitmapWrapper()
        val targetRadius = 10f
        val colorInt = -65536
        val colorName = "Red"

        Mockito.`when`(getTargetAreaColorUseCase(bitmapWrapper, targetRadius)).thenReturn(Result.success(Pair(colorInt, colorName)))
        Mockito.`when`(timerIntervalUseCase(any())).thenReturn(true)
        Mockito.`when`(colorRepository.insertColor(any())).thenReturn(Unit)

        val expectedState = CameraFeedState(
            colorPresentationModel = ColorPresentationModel(colorInt, colorName)
        )

        viewModel.state.test {
            awaitItem() // initial state
            viewModel.dispatch(CameraFeedIntent.OnFrameAnalyze(bitmapWrapper, targetRadius))
            assertEquals(awaitItem(), expectedState)
        }
    }

    @Test
    fun `on dispatch OnFrameAnalyze intent, update state with error message if getTargetAreaColorUseCase fails`() = runTest {
        val bitmapWrapper = BitmapWrapper()
        val targetRadius = 10f
        val errorMessage = "Error getting target area color"

        Mockito.`when`(getTargetAreaColorUseCase(bitmapWrapper, targetRadius)).thenReturn(Result.failure(Exception(errorMessage)))
        Mockito.`when`(timerIntervalUseCase(any())).thenReturn(true)

        val expectedState = CameraFeedState(
            colorPresentationModel = ColorPresentationModel(null, "ERROR")
        )

        viewModel.state.test {
            awaitItem() // initial state
            viewModel.dispatch(CameraFeedIntent.OnFrameAnalyze(bitmapWrapper, targetRadius))
            assertEquals(awaitItem(), expectedState)
        }
    }

    @Test
    fun `on dispatch OnFrameAnalyze intent, nothing happens if timerIntervalUseCase fails`() = runTest {
        Mockito.`when`(timerIntervalUseCase(any())).thenReturn(false)
        viewModel.state.test {
            viewModel.dispatch(CameraFeedIntent.OnFrameAnalyze(BitmapWrapper(), 10f))
            // state is equal to initial state
            assertEquals(awaitItem(), initialState)
        }
    }

    @Test
    fun `on dispatch OnStartAnalysis intent, update state with isAnalyzing set to true and send StartFrameAnalysis side effect`() = runTest {
        val expectedState = CameraFeedState(isAnalyzing = true)

        turbineScope {
            val sideEffects = viewModel.sideEffects.testIn(backgroundScope)
            val state = viewModel.state.testIn(backgroundScope)
            state.awaitItem() // initial state

            viewModel.dispatch(CameraFeedIntent.OnStartAnalysis)
            assertEquals(state.awaitItem(), expectedState)
            assertEquals(sideEffects.awaitItem(), CameraFeedEffect.StartFrameAnalysis)
        }
    }

    @Test
    fun `on dispatch OnStopAnalysis intent, update state with isAnalyzing set to false and send StopFrameAnalysis side effect`() = runTest {
        // setup viewModel with active analyzing state
        val initialState = CameraFeedState(isAnalyzing = true)
        viewModel = CameraFeedViewModel(getTargetAreaColorUseCase, timerIntervalUseCase, colorRepository, initialState)

        val expectedState = CameraFeedState(isAnalyzing = false)

        turbineScope {
            val sideEffects = viewModel.sideEffects.testIn(backgroundScope)
            val state = viewModel.state.testIn(backgroundScope)
            state.awaitItem() // initial state

            viewModel.dispatch(CameraFeedIntent.OnStopAnalysis)
            assertEquals(state.awaitItem(), expectedState)
            assertEquals(sideEffects.awaitItem(), CameraFeedEffect.StopFrameAnalysis)
        }
    }

    @Test
    fun `on dispatch OnCameraNotReady intent, send ShowToastCameraNotReady side effect`() = runTest {
        val expectedSideEffect = CameraFeedEffect.ShowToastCameraNotReady
        viewModel.sideEffects.test {
            viewModel.dispatch(CameraFeedIntent.OnCameraNotReady)
            assertEquals(awaitItem(), expectedSideEffect)
        }
    }

    @Test
    fun `on dispatch OnGoToColorHistory intent, send GoToColorHistory side effect`() = runTest {
        val expectedSideEffect = CameraFeedEffect.GoToColorHistory
        viewModel.sideEffects.test {
            viewModel.dispatch(CameraFeedIntent.OnGoToColorHistory)
            assertEquals(awaitItem(), expectedSideEffect)
        }
    }
}