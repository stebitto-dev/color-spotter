package com.stebitto.feature_color_history.models

import android.graphics.Color
import com.stebitto.common.data.ColorDTO

internal data class ColorPresentationModel(
    val id: Int,
    val name: String,
    val lastSeen: Long,
    val colorInt: Int
) {
    val hexCode: String = Integer.toHexString(colorInt).substring(2)
    val red: Int = Color.red(colorInt)
    val green: Int = Color.green(colorInt)
    val blue: Int = Color.blue(colorInt)

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
        colorInt = colorInt
    )
}