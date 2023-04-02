package com.dawidk.settings

import androidx.lifecycle.viewModelScope
import com.dawidk.common.mvi.BaseViewModel
import com.dawidk.core.datastore.UserCredentialsDataStoreRepository
import com.dawidk.core.domain.mappers.mapToAccountInfo
import com.dawidk.settings.mvi.SettingsAction
import com.dawidk.settings.mvi.SettingsEvent
import com.dawidk.settings.mvi.SettingsState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userCredentialsDataStoreRepository: UserCredentialsDataStoreRepository
) : BaseViewModel<SettingsEvent, SettingsAction, SettingsState>(SettingsState.Loading) {

    override fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.Init -> loadUserData()
            SettingsAction.Load -> updateState(SettingsState.Loading)
            is SettingsAction.DataLoaded -> {
                if (state.value is SettingsState.Loading) {
                    updateState(SettingsState.DataLoaded(action.data))
                }
            }
            is SettingsAction.NavigateToSignInScreen -> navigateToSignInFragment()
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            userCredentialsDataStoreRepository.userCredentialsFlow.collectLatest {
                updateState(SettingsState.DataLoaded(it.mapToAccountInfo()))
            }
        }
    }

    private fun navigateToSignInFragment() {
        viewModelScope.launch {
            launch {
                userCredentialsDataStoreRepository.clearUserCredentials()
            }.join()
            emitEvent(SettingsEvent.NavigateToSignInScreen)
        }
    }
}