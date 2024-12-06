package com.stebitto.feature_color_history

import app.cash.turbine.test
import com.stebitto.common.MainDispatcherRule
import com.stebitto.common.data.ColorDTO
import com.stebitto.common.data.ColorRepository
import com.stebitto.feature_color_history.models.ColorPresentationModel
import com.stebitto.feature_color_history.presentation.ColorHistoryIntent
import com.stebitto.feature_color_history.presentation.ColorHistoryState
import com.stebitto.feature_color_history.presentation.ColorHistoryViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class ColorHistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var colorRepository: ColorRepository

    private lateinit var viewModel: ColorHistoryViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = ColorHistoryViewModel(colorRepository)
    }

    @Test
    fun `test initial state`() {
        val initialState = ColorHistoryState()
        viewModel = ColorHistoryViewModel(colorRepository, initialState)
        assertEquals(viewModel.state.value, ColorHistoryState())
    }

    @Test
    fun `dispatch LoadColors intent, update state with colors`() = runTest {
        val colorDTO = ColorDTO(1, "Red", 1234567890, 123456)
        val colorPresentationModel = ColorPresentationModel(1, "Red", 1234567890, 123456)
        val colorsDTO = listOf(colorDTO)
        val colorsPresentationModel = listOf(colorPresentationModel)

        val expectedState = ColorHistoryState(colors = colorsPresentationModel)

        Mockito.`when`(colorRepository.getAllColors()).thenReturn(flowOf(colorsDTO))

        viewModel.state.test {
            awaitItem() // initial state
            viewModel.dispatch(ColorHistoryIntent.LoadColors)
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `dispatch LoadColors intent, update state with error message if repository throws exception`() = runTest {
        val errorMessage = "Error loading colors"
        val expectedState = ColorHistoryState(errorMessage = errorMessage)
        val mock = mock<ColorRepository> {
            on { getAllColors() } doReturn flow { throw RuntimeException(errorMessage) }
        }
        viewModel = ColorHistoryViewModel(mock)

        viewModel.state.test {
            awaitItem() // initial state
            viewModel.dispatch(ColorHistoryIntent.LoadColors)
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `dispatch OnSortColors intent, update state with sortAlphabetically set to true`() = runTest {
        val expectedState = ColorHistoryState(sortAlphabetically = true)
        viewModel.state.test {
            awaitItem() // initial state
            viewModel.dispatch(ColorHistoryIntent.OnSortColors)
            assertEquals(expectedState, awaitItem())
        }
    }
}