package com.stebitto.feature_camera_feed

import com.stebitto.common.MainDispatcherRule
import com.stebitto.feature_camera_feed.data.ColorNameRemoteSource
import com.stebitto.feature_camera_feed.data.ColorNameRepositoryImpl
import com.stebitto.feature_camera_feed.models.ColorNameRemoteModel
import com.stebitto.feature_camera_feed.models.NameRemoteModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class ColorNameRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var colorNameRemoteSource: ColorNameRemoteSource

    private lateinit var colorNameRepository: ColorNameRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        colorNameRepository = ColorNameRepositoryImpl(colorNameRemoteSource)
    }

    @Test
    fun `getColorName should return the correct color name`() = runTest {
        val colorName = "Test Color"
        val remoteModel = ColorNameRemoteModel(NameRemoteModel(colorName, "#000000"))
        Mockito.`when`(colorNameRemoteSource.getColorName(123, 456, 789)).thenReturn(remoteModel)
        val result = colorNameRepository.getColorName(123.0, 456.0, 789.0)
        assert(result.isSuccess)
        assertEquals(result.getOrNull(), colorName)
    }

    @Test
    fun `getColorName should return an error when the remote source throws an exception`() = runTest {
        val exception = RuntimeException("Test exception")
        Mockito.`when`(colorNameRemoteSource.getColorName(123, 456, 789)).thenThrow(exception)
        val result = colorNameRepository.getColorName(123.0, 456.0, 789.0)
        assert(result.isFailure)
        assertEquals(result.exceptionOrNull(), exception)
    }
}