package com.stebitto.feature_color_history.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stebitto.common.data.ColorDTO

@Entity(tableName = "colors")
internal data class ColorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
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
            hexCode = hexCode,
            red = red,
            green = green,
            blue = blue
        )
    }
}

internal fun ColorDTO.toColorEntity(): ColorEntity {
    return ColorEntity(
        id = id,
        name = name,
        lastSeen = lastSeen,
        hexCode = hexCode,
        red = red,
        green = green,
        blue = blue
    )
}