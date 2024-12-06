package com.stebitto.common.data

import kotlinx.coroutines.flow.Flow

interface ColorRepository {
    fun getAllColors(): Flow<List<ColorDTO>>
    suspend fun insertColor(color: ColorDTO)
    suspend fun deleteColor(id: Int)
}