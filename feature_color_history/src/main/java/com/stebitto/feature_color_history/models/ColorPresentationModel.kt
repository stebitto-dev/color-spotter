package com.stebitto.feature_color_history.models

import android.graphics.Color
import com.stebitto.common.data.ColorDTO

internal data class ColorPresentationModel(
    val id: Int,
    val name: String,
    val lastSeen: Long,
    val colorInt: Int
) {
    fun getColorHex(): String = Integer.toHexString(colorInt).substring(2)
    fun getRed(): Int = Color.red(colorInt)
    fun getGreen(): Int = Color.green(colorInt)
    fun getBlue(): Int = Color.blue(colorInt)

    fun toColorDTO(): ColorDTO {
        return ColorDTO(
            id = id,
            name = name,
            lastSeen = lastSeen,
            colorInt = Color.rgb(getRed(), getGreen(), getBlue())
        )
    }
}

internal fun ColorDTO.toColorPresentationModel(): ColorPresentationModel {
    return ColorPresentationModel(
        id = id,
        name = name,
        lastSeen = lastSeen,
        colorInt = colorInt
    )
}