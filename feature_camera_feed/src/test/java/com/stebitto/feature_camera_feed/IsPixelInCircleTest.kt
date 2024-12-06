package com.stebitto.feature_camera_feed

import com.stebitto.feature_camera_feed.data.ColorNameRepository
import com.stebitto.feature_camera_feed.data.GetTargetAreaColorUseCaseImpl
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class IsPixelInCircleTest {

    @Mock
    private lateinit var colorNameRepository: ColorNameRepository

    private lateinit var getTargetAreaColorUseCase: GetTargetAreaColorUseCaseImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getTargetAreaColorUseCase = GetTargetAreaColorUseCaseImpl(colorNameRepository)
    }

    @Test
    fun `isPixelInCircle should return true when point is the center`() {
        val x = 0f
        val y = 0f
        val centerX = 0f
        val centerY = 0f
        val radius = 10f
        val result = getTargetAreaColorUseCase.isPixelInCircle(x, y, centerX, centerY, radius)
        assert(result)
    }

    @Test
    fun `isPixelInCircle should return true when point is the center and radius is 0`() {
        val x = 0f
        val y = 0f
        val centerX = 0f
        val centerY = 0f
        val radius = 0f
        val result = getTargetAreaColorUseCase.isPixelInCircle(x, y, centerX, centerY, radius)
        assert(result)
    }

    @Test
    fun `isPixelInCircle should return true when point is on the circumference of the circle`() {
        val x = -10f
        val y = 0f
        val centerX = 0f
        val centerY = 0f
        val radius = 10f
        val result = getTargetAreaColorUseCase.isPixelInCircle(x, y, centerX, centerY, radius)
        assert(result)
    }

    @Test
    fun `isPixelInCircle should return false when point is at the edge of square circumscribed to the circle`() {
        val x = 10f
        val y = 10f
        val centerX = 0f
        val centerY = 0f
        val radius = 10f
        val result = getTargetAreaColorUseCase.isPixelInCircle(x, y, centerX, centerY, radius)
        assert(!result)
    }

    @Test
    fun `isPixelInCircle should return true when point is inside the circle`() {
        val x = 7f
        val y = 7f
        val centerX = 0f
        val centerY = 0f
        val radius = 10f
        val result = getTargetAreaColorUseCase.isPixelInCircle(x, y, centerX, centerY, radius)
        assert(result)
    }

    @Test
    fun `isPixelInCircle should return false when point is outside the circle`() {
        val x = 7.5f
        val y = 7.5f
        val centerX = 0f
        val centerY = 0f
        val radius = 10f
        val result = getTargetAreaColorUseCase.isPixelInCircle(x, y, centerX, centerY, radius)
        assert(!result)
    }
}