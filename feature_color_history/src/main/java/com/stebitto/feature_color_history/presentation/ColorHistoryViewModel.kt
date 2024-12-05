package com.stebitto.feature_color_history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stebitto.common.MVIViewModel
import com.stebitto.common.stateInWhileSubscribed
import com.stebitto.common.data.ColorRepository
import com.stebitto.feature_color_history.models.toColorPresentationModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ColorHistoryViewModel(
    private val colorRepository: ColorRepository,
    initialState: ColorHistoryState = ColorHistoryState()
) : ViewModel(), MVIViewModel<ColorHistoryState, ColorHistoryIntent, ColorHistoryEffect> {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<ColorHistoryState> = _state.stateInWhileSubscribed(initialState)

    private val _sideEffects = Channel<ColorHistoryEffect>()
    override val sideEffects: Flow<ColorHistoryEffect>
        get() = _sideEffects.receiveAsFlow()

    override fun dispatch(intent: ColorHistoryIntent) {
        viewModelScope.launch {
            when (intent) {
                is ColorHistoryIntent.LoadColors -> {
                    colorRepository.getAllColors()
                        .map { colorListDTO -> colorListDTO.map { it.toColorPresentationModel() } }
                        .catch { _state.value = _state.value.copy(colors = emptyList()) }
                        .collect { colorListPresentationModel ->
                            _state.value =
                                _state.value.copy(colors = colorListPresentationModel)
                        }
                }
                is ColorHistoryIntent.OnSortColors -> {
                    _state.update { it.copy(sortAlphabetically = true) }
                }
                is ColorHistoryIntent.OnDeleteColor -> {
                    colorRepository.deleteColor(intent.color.id)
                }
            }
        }
    }
}