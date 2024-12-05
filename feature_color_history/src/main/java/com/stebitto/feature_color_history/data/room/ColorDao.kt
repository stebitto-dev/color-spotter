package com.stebitto.feature_color_history.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.stebitto.feature_color_history.models.ColorEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ColorDao {

    @Query("SELECT * FROM colors ORDER BY lastSeen DESC")
    fun getAllColors(): Flow<List<ColorEntity>>

    @Insert
    suspend fun insertColor(color: ColorEntity)

    @Delete
    suspend fun deleteColor(color: ColorEntity)
}