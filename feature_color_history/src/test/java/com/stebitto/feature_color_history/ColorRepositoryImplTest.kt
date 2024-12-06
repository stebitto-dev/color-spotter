package com.stebitto.feature_color_history

import app.cash.turbine.test
import com.stebitto.common.data.ColorDTO
import com.stebitto.feature_color_history.data.ColorLocalSource
import com.stebitto.feature_color_history.data.ColorRepositoryImpl
import com.stebitto.feature_color_history.models.ColorEntity
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class ColorRepositoryImplTest {

    @Mock
    private lateinit var colorLocalSource: ColorLocalSource

    private lateinit var colorRepository: ColorRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        colorRepository = ColorRepositoryImpl(colorLocalSource)
    }

    @Test
    fun `getAllColors should return a list of colors`() = runTest {
        val colorEntity = ColorEntity(1, "Red", 1234567890, 123456)
        val colorDTO = ColorDTO(1, "Red", 1234567890, 123456)
        val colors = listOf(colorEntity)
        Mockito.`when`(colorLocalSource.getAllColors()).thenReturn(flowOf(colors))
        colorRepository.getAllColors().test {
            val result = awaitItem()
            assertEquals(result, listOf(colorDTO))
            awaitComplete()
        }
    }

    @Test
    fun `getAllColors should return an empty list if no colors are found`() = runTest {
        Mockito.`when`(colorLocalSource.getAllColors()).thenReturn(flowOf(emptyList()))
        colorRepository.getAllColors().test {
            val result = awaitItem()
            assertEquals(result, emptyList<ColorDTO>())
            awaitComplete()
        }
    }
}