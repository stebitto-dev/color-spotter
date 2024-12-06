package com.stebitto.feature_camera_feed.models

import android.graphics.Bitmap
import com.stebitto.feature_camera_feed.presentation.CameraFeedViewModel

/**
 * Wrapper class for a [Bitmap] object.
 * Used by [CameraFeedViewModel] so that there is no direct dependency to Android framework.
 */
data class BitmapWrapper(
    val bitmap: Bitmap
)