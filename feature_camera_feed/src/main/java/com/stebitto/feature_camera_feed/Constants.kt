package com.stebitto.feature_camera_feed

/**
 * Radius of ROI (Region of Interest) analyzed by the camera.
 * Used by [CameraPreview]
 */
internal const val TARGET_RADIUS = 100f

/**
 * Time interval between frames analyzed by the camera.
 * Used by [CameraFeedViewModel].
 */
internal const val CAPTURE_ANALYSIS_INTERVAL = 1000L