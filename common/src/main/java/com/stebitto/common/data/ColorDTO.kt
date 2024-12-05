package com.stebitto.common.data

data class ColorDTO(
    val id: Int = 0,
    val name: String,
    val lastSeen: Long,
    val hexCode: String,
    val red: Int,
    val green: Int,
    val blue: Int
)