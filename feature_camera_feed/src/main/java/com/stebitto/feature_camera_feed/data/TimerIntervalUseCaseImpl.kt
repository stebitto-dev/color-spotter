package com.stebitto.feature_camera_feed.data

import java.util.Date

internal class TimerIntervalUseCaseImpl : TimerIntervalUseCase {

    private var lastSeen = Date()
    override suspend fun invoke(interval: Long): Boolean {
        return if (Date().time - lastSeen.time >= interval) {
            lastSeen = Date()
            true
        } else {
            false
        }
    }
}