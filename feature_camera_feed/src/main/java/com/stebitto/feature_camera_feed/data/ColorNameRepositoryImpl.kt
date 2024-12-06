package com.stebitto.feature_camera_feed.data

internal class ColorNameRepositoryImpl(
    private val colorNameRemoteSource: ColorNameRemoteSource
) : ColorNameRepository {
    override suspend fun getColorName(red: Double, green: Double, blue: Double): Result<String> = runCatching {
        return Result.success(
            colorNameRemoteSource
                .getColorName(red.toInt(), green.toInt(), blue.toInt()).name.value
        )
    }
}