package com.stebitto.feature_color_history.di

import android.app.Application
import androidx.room.Room
import com.stebitto.common.data.ColorRepository
import com.stebitto.feature_color_history.data.ColorLocalSource
import com.stebitto.feature_color_history.data.ColorLocalSourceImpl
import com.stebitto.feature_color_history.data.ColorRepositoryImpl
import com.stebitto.feature_color_history.data.room.ColorDatabase
import com.stebitto.feature_color_history.presentation.ColorHistoryViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val localDbModule = module {
    single { provideDatabase(androidApplication()) }
    single { provideDao(get()) }
}

val featureColorHistoryModule = module {
    viewModel { ColorHistoryViewModel(get()) }
    factory<ColorRepository> { ColorRepositoryImpl(get()) }
    factory<ColorLocalSource> { ColorLocalSourceImpl(get()) }
    includes(localDbModule)
}

internal fun provideDatabase(application: Application): ColorDatabase {
    return Room.databaseBuilder(
        application,
        ColorDatabase::class.java,
        "color_database"
    ).build()
}

internal fun provideDao(database: ColorDatabase) = database.colorDao()