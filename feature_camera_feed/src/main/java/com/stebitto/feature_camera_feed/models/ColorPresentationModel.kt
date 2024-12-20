package com.stebitto.feature_camera_feed.models

import android.graphics.Color

internal data class ColorPresentationModel(
    val colorInt: Int? = null,
    val colorName: String = ""
) {
    fun getColorHex(): String = colorInt?.let { Integer.toHexString(it).substring(2) } ?: ""
    fun getRed(): Int = colorInt?.let { Color.red(it) } ?: -1
    fun getGreen(): Int = colorInt?.let { Color.green(it) } ?: -1
    fun getBlue(): Int = colorInt?.let { Color.blue(it) } ?: -1
}