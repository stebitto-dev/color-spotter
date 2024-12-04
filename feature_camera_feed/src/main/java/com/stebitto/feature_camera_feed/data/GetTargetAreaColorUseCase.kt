package com.stebitto.feature_camera_feed.data

import android.graphics.Bitmap

interface GetTargetAreaColorUseCase {
    suspend operator fun invoke(bitmap: Bitmap, targetRadius: Float): Result<Pair<Int, String>>
}