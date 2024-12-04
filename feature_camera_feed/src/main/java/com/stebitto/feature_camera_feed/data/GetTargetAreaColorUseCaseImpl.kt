package com.stebitto.feature_camera_feed.data

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.VisibleForTesting

internal class GetTargetAreaColorUseCaseImpl(
    private val colorNameRepository: ColorNameRepository
) : GetTargetAreaColorUseCase {

    override suspend fun invoke(bitmap: Bitmap, targetRadius: Float): Result<Pair<Color, String>> = runCatching {
        return Result.success(
            withContext(Dispatchers.IO) {
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