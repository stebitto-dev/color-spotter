package com.stebitto.feature_camera_feed.data

import com.stebitto.feature_camera_feed.models.BitmapWrapper

internal interface GetTargetAreaColorUseCase {
    suspend operator fun invoke(bitmapWrapper: BitmapWrapper, targetRadius: Float): Result<Pair<Int, String>>
}