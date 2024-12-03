package com.stebitto.feature_camera_feed.data.retrofit

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

internal interface ColorAPIService {
    @GET("id")
    suspend fun getColorName(@Query("rgb", encoded = true) rgb: String): ColorRemoteModel
}

internal data class ColorRemoteModel(
    val name: NameRemoteModel
)

internal data class NameRemoteModel(
    val value: String,
    @SerializedName("closest_named_hex") val closestNamedHex: String
)