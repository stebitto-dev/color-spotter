package com.stebitto.feature_color_history.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stebitto.feature_color_history.models.ColorEntity

@Database(entities = [ColorEntity::class], version = 1, exportSchema = false)
internal abstract class ColorDatabase : RoomDatabase() {
    abstract fun colorDao(): ColorDao
}