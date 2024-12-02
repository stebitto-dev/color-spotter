package com.stebitto.common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController

val LightGreyScaleColorScheme = lightColorScheme(
    primary = GreyScalePalette.Grey800,
    onPrimary = GreyScalePalette.Grey200,
    primaryContainer = GreyScalePalette.Grey300,
    onPrimaryContainer = GreyScalePalette.Grey900,
    secondary = GreyScalePalette.Grey600,
    onSecondary = GreyScalePalette.Grey300,
    secondaryContainer = GreyScalePalette.Grey400,
    onSecondaryContainer = GreyScalePalette.Grey900,
    tertiary = GreyScalePalette.Grey400,
    onTertiary = GreyScalePalette.Grey800,
    tertiaryContainer = GreyScalePalette.Grey500,
    onTertiaryContainer = GreyScalePalette.Grey900,
    error = GreyScalePalette.Grey800,
    onError = GreyScalePalette.Grey200,
    errorContainer = GreyScalePalette.Grey300,
    onErrorContainer = GreyScalePalette.Grey900,
    background = GreyScalePalette.Grey100,
    onBackground = GreyScalePalette.Grey900,
    surface = GreyScalePalette.Grey200,
    onSurface = GreyScalePalette.Grey800,
    surfaceVariant = GreyScalePalette.Grey300,
    onSurfaceVariant = GreyScalePalette.Grey800,
    outline = GreyScalePalette.Grey600,
    inverseOnSurface = GreyScalePalette.Grey200,
    inverseSurface = GreyScalePalette.Grey800,
    inversePrimary = GreyScalePalette.Grey300
)

val DarkGreyScaleColorScheme = darkColorScheme(
    primary = GreyScalePalette.Grey300,
    onPrimary = GreyScalePalette.Grey900,
    primaryContainer = GreyScalePalette.Grey700,
    onPrimaryContainer = GreyScalePalette.Grey100,
    secondary = GreyScalePalette.Grey500,
    onSecondary = GreyScalePalette.Grey800,
    secondaryContainer = GreyScalePalette.Grey600,
    onSecondaryContainer = GreyScalePalette.Grey100,
    tertiary = GreyScalePalette.Grey600,
    onTertiary = GreyScalePalette.Grey300,
    tertiaryContainer = GreyScalePalette.Grey500,
    onTertiaryContainer = GreyScalePalette.Grey100,
    error = GreyScalePalette.Grey300,
    onError = GreyScalePalette.Grey900,
    errorContainer = GreyScalePalette.Grey700,
    onErrorContainer = GreyScalePalette.Grey100,
    background = GreyScalePalette.Grey900,
    onBackground = GreyScalePalette.Grey100,
    surface = GreyScalePalette.Grey800,
    onSurface = GreyScalePalette.Grey200,
    surfaceVariant = GreyScalePalette.Grey700,
    onSurfaceVariant = GreyScalePalette.Grey300,
    outline = GreyScalePalette.Grey500,
    inverseOnSurface = GreyScalePalette.Grey800,
    inverseSurface = GreyScalePalette.Grey200,
    inversePrimary = GreyScalePalette.Grey800
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkGreyScaleColorScheme
    } else {
        LightGreyScaleColorScheme
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setNavigationBarColor(
            color = if (darkTheme) GreyScalePalette.Grey700 else GreyScalePalette.Grey300
        )
        systemUiController.setStatusBarColor(
            color = if (darkTheme) GreyScalePalette.Grey700 else GreyScalePalette.Grey300
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}