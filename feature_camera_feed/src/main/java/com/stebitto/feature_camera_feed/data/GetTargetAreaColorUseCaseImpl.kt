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
                val xCoordinate = bitmap.width / 2f
                val yCoordinate = bitmap.height / 2f

                val redsList = mutableListOf<Int>()
                val bluesList = mutableListOf<Int>()
                val greensList = mutableListOf<Int>()

                for (x in (xCoordinate - targetRadius).toInt()..(xCoordinate + targetRadius).toInt()) {
                    for (y in (yCoordinate - targetRadius).toInt()..(yCoordinate + targetRadius).toInt()) {
                        val point = bitmap.getPixel(x, y)
                        if (isPixelInCircle(x.toFloat(), y.toFloat(), xCoordinate, yCoordinate, targetRadius)) {
                            redsList.add(Color.red(point))
                            greensList.add(Color.green(point))
                            bluesList.add(Color.blue(point))
                        }
                    }
                }

                val redAverage = redsList.average()
                val greenAverage = greensList.average()
                val blueAverage = bluesList.average()

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