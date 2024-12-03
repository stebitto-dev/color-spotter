package com.stebitto.feature_camera_feed.data

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color

interface GetTargetAreaColorUseCase {
    suspend operator fun invoke(bitmap: Bitmap, targetRadius: Float): Result<Pair<Color, String>>
}