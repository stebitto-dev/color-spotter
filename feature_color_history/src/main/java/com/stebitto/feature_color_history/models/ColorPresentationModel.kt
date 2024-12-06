package com.stebitto.feature_color_history.models

import android.graphics.Color
import com.stebitto.common.data.ColorDTO

internal data class ColorPresentationModel(
    val id: Int,
    val name: String,
    val lastSeen: Long,
    val hexCode: String,
    val red: Int,
    val green: Int,
    val blue: Int
) {
    fun toColorDTO(): ColorDTO {
        return ColorDTO(
            id = id,
            name = name,
            lastSeen = lastSeen,
            colorInt = Color.rgb(red, green, blue)
        )
    }
}

internal fun ColorDTO.toColorPresentationModel(): ColorPresentationModel {
    return ColorPresentationModel(
        id = id,
        name = name,
        lastSeen = lastSeen,
        hexCode = Integer.toHexString(colorInt).substring(2),
        red = Color.red(colorInt),
        green = Color.green(colorInt),
        blue = Color.blue(colorInt)
    )
}