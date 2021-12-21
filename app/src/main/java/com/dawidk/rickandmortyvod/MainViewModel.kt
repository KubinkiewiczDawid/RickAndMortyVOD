package com.dawidk.rickandmortyvod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawidk.rickandmortyvod.state.MainAction
import com.dawidk.rickandmortyvod.state.MainEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _event: MutableSharedFlow<MainEvent> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<MainEvent> = _event

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.NavigateToSettingsScreen -> navigateToSettingsScreen()
        }
    }

    private fun navigateToSettingsScreen() {
        viewModelScope.launch {
            _event.tryEmit(MainEvent.NavigateToSettingsScreen)
        }
    }
}