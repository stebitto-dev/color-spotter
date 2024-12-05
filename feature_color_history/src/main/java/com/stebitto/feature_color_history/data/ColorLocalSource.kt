package com.stebitto.feature_color_history.data

import com.stebitto.feature_color_history.models.ColorEntity
import kotlinx.coroutines.flow.Flow

internal interface ColorLocalSource {
    fun getAllColors(): Flow<List<ColorEntity>>
    suspend fun insertColor(color: ColorEntity)
    suspend fun deleteColor(color: ColorEntity)
}