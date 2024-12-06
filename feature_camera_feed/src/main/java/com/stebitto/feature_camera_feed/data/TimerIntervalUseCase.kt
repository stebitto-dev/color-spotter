package com.stebitto.feature_camera_feed.data

interface TimerIntervalUseCase {
    suspend operator fun invoke(interval: Long): Boolean
}