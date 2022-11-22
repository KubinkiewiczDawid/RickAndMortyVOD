package com.dawidk.common.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<EVENT: ViewEvent, ACTION : ViewAction, STATE: ViewState>(initialState: STATE): ViewModel() {

    private val _state: MutableStateFlow<STATE> = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state
    private val _event: MutableSharedFlow<EVENT> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<EVENT> = _event

    abstract fun onAction(action: ACTION)

    protected fun updateState(state: STATE) {
        _state.value = state
    }

    protected fun emitEvent(event: EVENT) {
        _event.tryEmit(event)
    }

}
