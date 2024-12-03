package com.stebitto.feature_camera_feed.di

import com.stebitto.feature_camera_feed.presentation.CameraFeedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val cameraFeedModule = module {
    viewModel { CameraFeedViewModel() }
}