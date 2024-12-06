package com.stebitto.feature_camera_feed.data.retrofit

import com.stebitto.feature_camera_feed.models.ColorNameRemoteModel
import retrofit2.http.GET
import retrofit2.http.Query

internal interface ColorAPIService {
    @GET("id")
    suspend fun getColorName(@Query("rgb", encoded = true) rgb: String): ColorNameRemoteModel
}