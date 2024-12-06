package com.stebitto.feature_camera_feed.data

import com.stebitto.feature_camera_feed.models.ColorNameRemoteModel

internal interface ColorNameRemoteSource {
    suspend fun getColorName(red: Int, green: Int, blue: Int): ColorNameRemoteModel
}