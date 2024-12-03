package com.stebitto.feature_camera_feed.data

import com.stebitto.feature_camera_feed.data.retrofit.ColorRemoteModel

internal interface ColorNameRemoteSource {
    suspend fun getColorName(red: Int, green: Int, blue: Int): ColorRemoteModel
}