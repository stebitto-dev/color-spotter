package com.stebitto.feature_camera_feed.data

import android.graphics.Color
import com.stebitto.feature_camera_feed.models.BitmapWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.VisibleForTesting

internal class GetTargetAreaColorUseCaseImpl(
    private val colorNameRepository: ColorNameRepository
) : GetTargetAreaColorUseCase {

    override suspend fun invoke(bitmapWrapper: BitmapWrapper, targetRadius: Float): Result<Pair<Int, String>> = runCatching {
        return Result.success(
            withContext(Dispatchers.IO) {
                val bitmap = bitmapWrapper.bitmap ?: throw IllegalStateException("Bitmap is null")
                val centerXCoordinate = bitmap.width / 2f
                val centerYCoordinate = bitmap.height / 2f

                var redSum = 0
                var greenSum = 0
                var blueSum = 0
                var pixelCount = 0

                // cycle through the square identified by the target radius
                for (x in (centerXCoordinate - targetRadius).toInt()..(centerXCoordinate + targetRadius).toInt()) {
                    for (y in (centerYCoordinate - targetRadius).toInt()..(centerYCoordinate + targetRadius).toInt()) {
                        // take in consideration only pixels inside the target radius
                        if (isPixelInCircle(x.toFloat(), y.toFloat(), centerXCoordinate, centerYCoordinate, targetRadius)) {
                            val point = bitmap.getPixel(x, y)
                            redSum += Color.red(point)
                            greenSum += Color.green(point)
                            blueSum += Color.blue(point)
                            pixelCount++
                        }
                    }
                }

                // calculate the average color
                val redAverage = redSum / pixelCount.toFloat()
                val greenAverage = greenSum / pixelCount.toFloat()
                val blueAverage = blueSum / pixelCount.toFloat()

                val color = Color.rgb(redAverage.toInt(), greenAverage.toInt(), blueAverage.toInt())
                color to colorNameRepository.getColorName(redAverage, greenAverage, blueAverage).getOrThrow()
            }
        )
    }

    @VisibleForTesting
    internal fun isPixelInCircle(
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
}