package com.stebitto.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MVIViewModel<T : State, U : Intent, S : Effect> {
    val state: StateFlow<T>
    val sideEffects: Flow<S>
    fun dispatch(intent: U)
}

interface State

interface Intent

interface Effect