package com.dawidk.settings.state

import com.dawidk.core.domain.model.AccountInfo

sealed class SettingsState {
    object Loading : SettingsState()
    data class DataLoaded(val data: AccountInfo) : SettingsState()
    data class Error(val exception: Throwable) : SettingsState()
}