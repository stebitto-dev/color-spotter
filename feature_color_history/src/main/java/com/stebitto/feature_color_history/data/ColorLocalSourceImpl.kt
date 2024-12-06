package com.stebitto.feature_color_history.data

import com.stebitto.feature_color_history.data.room.ColorDao
import com.stebitto.feature_color_history.models.ColorEntity
import kotlinx.coroutines.flow.Flow

internal class ColorLocalSourceImpl(
    private val colorDao: ColorDao
): ColorLocalSource {
    override fun getAllColors(): Flow<List<ColorEntity>> {
        return colorDao.getAllColors()
    }

    override suspend fun insertColor(color: ColorEntity) {
        colorDao.insertColor(color)
    }

    override suspend fun deleteColor(id: Int) {
        colorDao.deleteColorById(id)
    }
}