package com.stebitto.feature_camera_feed.data

import com.stebitto.feature_camera_feed.data.retrofit.ColorAPIService
import com.stebitto.feature_camera_feed.models.ColorNameRemoteModel

internal class ColorNameRemoteSourceImpl(
    private val colorAPIService: ColorAPIService
) : ColorNameRemoteSource {

    override suspend fun getColorName(red: Int, green: Int, blue: Int): ColorNameRemoteModel =
        colorAPIService.getColorName("$red,$green,$blue")
}