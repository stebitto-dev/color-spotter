package com.stebitto.feature_camera_feed.data

import com.stebitto.feature_camera_feed.CAPTURE_ANALYSIS_INTERVAL
import java.util.Date

internal class TimerIntervalUseCaseImpl : TimerIntervalUseCase {

    private var lastSeen = Date()
    override suspend fun invoke(interval: Long): Boolean {
        return if (Date().time - lastSeen.time < CAPTURE_ANALYSIS_INTERVAL) {
            lastSeen = Date()
            true
        } else {
            false
        }
    }
}