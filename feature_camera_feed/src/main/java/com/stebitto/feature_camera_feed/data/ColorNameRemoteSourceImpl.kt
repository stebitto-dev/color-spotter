package com.stebitto.feature_camera_feed.data

import com.stebitto.feature_camera_feed.data.retrofit.ColorAPIService
import com.stebitto.feature_camera_feed.data.retrofit.ColorRemoteModel

internal class ColorNameRemoteSourceImpl(
    private val colorAPIService: ColorAPIService
) : ColorNameRemoteSource {

    override suspend fun getColorName(red: Int, green: Int, blue: Int): ColorRemoteModel =
        colorAPIService.getColorName("$red,$green,$blue")
}