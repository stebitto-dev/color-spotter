package com.stebitto.feature_color_history.data

import com.stebitto.common.data.ColorDTO
import com.stebitto.common.data.ColorRepository
import com.stebitto.feature_color_history.models.toColorEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ColorRepositoryImpl(
    private val colorLocalSource: ColorLocalSource
) : ColorRepository {

    override fun getAllColors(): Flow<List<ColorDTO>> {
        return colorLocalSource.getAllColors()
            .map { colors -> colors.map { it.toColorDTO() } }
    }

    override suspend fun insertColor(color: ColorDTO) {
        colorLocalSource.insertColor(color.toColorEntity())
    }

    override suspend fun deleteColor(color: ColorDTO) {
        colorLocalSource.deleteColor(color.toColorEntity())
    }
}