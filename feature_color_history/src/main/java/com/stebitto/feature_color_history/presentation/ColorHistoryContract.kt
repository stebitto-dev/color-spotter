package com.stebitto.feature_color_history.presentation

import com.stebitto.common.Effect
import com.stebitto.common.Intent
import com.stebitto.common.State
import com.stebitto.feature_color_history.models.ColorPresentationModel

internal data class ColorHistoryState(
    val colors: List<ColorPresentationModel> = emptyList(),
    val sortAlphabetically: Boolean = false,
    val errorMessage: String? = null
) : State

internal sealed class ColorHistoryIntent : Intent {
    data object LoadColors : ColorHistoryIntent()
    data object OnSortColors : ColorHistoryIntent()
    data class OnDeleteColor(val color: ColorPresentationModel) : ColorHistoryIntent()
}

internal sealed class ColorHistoryEffect : Effect { }