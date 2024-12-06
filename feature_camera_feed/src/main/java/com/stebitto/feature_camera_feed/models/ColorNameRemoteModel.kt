package com.stebitto.feature_camera_feed.models

import com.google.gson.annotations.SerializedName

internal data class ColorNameRemoteModel(
    val name: NameRemoteModel
)

internal data class NameRemoteModel(
    val value: String,
    @SerializedName("closest_named_hex") val closestNamedHex: String
)