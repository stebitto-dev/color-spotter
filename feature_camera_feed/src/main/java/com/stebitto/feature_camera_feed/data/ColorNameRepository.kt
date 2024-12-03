package com.stebitto.feature_camera_feed.data

internal interface ColorNameRepository {
    suspend fun getColorName(red: Double, green: Double, blue: Double): Result<String>
}