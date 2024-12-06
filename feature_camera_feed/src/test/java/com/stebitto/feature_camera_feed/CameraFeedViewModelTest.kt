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
    fun `on dispatch OnStartAnalysis intent, update state with isAnalyzing set to true`() = runTest {
        val expectedState = CameraFeedState(isAnalyzing = true)

        viewModel.state.test {
            awaitItem() // initial state
            viewModel.dispatch(CameraFeedIntent.OnStartAnalysis)
            assertEquals(awaitItem(), expectedState)
        }
    }

    @Test
    fun `on dispatch OnStopAnalysis intent, update state with isAnalyzing set to false`() = runTest {
        // setup viewModel with active analyzing state
        val initialState = CameraFeedState(isAnalyzing = true)
        viewModel = CameraFeedViewModel(getTargetAreaColorUseCase, timerIntervalUseCase, colorRepository, initialState)

        val expectedState = CameraFeedState(isAnalyzing = false)

        viewModel.state.test {
            awaitItem() // initial state
            viewModel.dispatch(CameraFeedIntent.OnStopAnalysis)
            assertEquals(awaitItem(), expectedState)
        }
    }

    @Test
    fun `on dispatch OnGoToColorHistory intent, update state with isAnalyzing set to false and send GoToColorHistory side effect`() = runTest {
        val initialState = CameraFeedState(isAnalyzing = true)
        val expectedSideEffect = CameraFeedEffect.GoToColorHistory
        val expectedState = CameraFeedState(isAnalyzing = false)

        viewModel = CameraFeedViewModel(getTargetAreaColorUseCase, timerIntervalUseCase, colorRepository, initialState)

        turbineScope {
            val sideEffect = viewModel.sideEffects.testIn(backgroundScope)
            val state = viewModel.state.testIn(backgroundScope)
            state.awaitItem() // initial state
            viewModel.dispatch(CameraFeedIntent.OnGoToColorHistory)
            assertEquals(state.awaitItem(), expectedState)
            assertEquals(sideEffect.awaitItem(), expectedSideEffect)
        }
    }
}