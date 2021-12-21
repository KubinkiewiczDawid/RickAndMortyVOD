package com.dawidk.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawidk.core.datastore.UserCredentialsDataStoreRepository
import com.dawidk.core.domain.mappers.mapToAccountInfo
import com.dawidk.settings.state.SettingsAction
import com.dawidk.settings.state.SettingsEvent
import com.dawidk.settings.state.SettingsState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userCredentialsDataStoreRepository: UserCredentialsDataStoreRepository
) : ViewModel() {

    private val _state: MutableStateFlow<SettingsState> =
        MutableStateFlow(SettingsState.Loading)
    val state: StateFlow<SettingsState> = _state
    private val _event: MutableSharedFlow<SettingsEvent> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<SettingsEvent> = _event

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.Init -> loadUserData()
            SettingsAction.Load -> _state.value = SettingsState.Loading
            is SettingsAction.DataLoaded -> {
                if (_state.value is SettingsState.Loading) {
                    _state.value = SettingsState.DataLoaded(action.data)
                }
            }
            is SettingsAction.NavigateToSignInScreen -> viewModelScope.launch { navigateToSignInFragment() }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            userCredentialsDataStoreRepository.userCredentialsFlow.collect {
                _state.value = SettingsState.DataLoaded(it.mapToAccountInfo())
            }
        }
    }

    private suspend fun clearUserData() {
        viewModelScope.launch {
            userCredentialsDataStoreRepository.clearUserCredentials()
        }.join()
    }

    private suspend fun navigateToSignInFragment() {
        viewModelScope.launch {
            clearUserData()
        }.join()
        _event.tryEmit(SettingsEvent.NavigateToSignInScreen)
    }
}