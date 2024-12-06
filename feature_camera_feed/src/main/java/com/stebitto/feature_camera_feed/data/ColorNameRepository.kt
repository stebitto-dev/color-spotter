package com.stebitto.feature_camera_feed.data

internal interface ColorNameRepository {
    suspend fun getColorName(red: Float, green: Float, blue: Float): Result<String>
}