package com.stebitto.colorspotter

import android.app.Application
import com.stebitto.feature_camera_feed.di.featureCameraFeedModule
import com.stebitto.feature_color_history.di.featureColorHistoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(featureCameraFeedModule, featureColorHistoryModule)
        }
    }
}