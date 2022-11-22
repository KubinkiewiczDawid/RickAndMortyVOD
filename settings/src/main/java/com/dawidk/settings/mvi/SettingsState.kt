package com.dawidk.settings.mvi

import com.dawidk.common.mvi.ViewState
import com.dawidk.core.domain.model.AccountInfo

sealed class SettingsState: ViewState {
    object Loading : SettingsState()
    data class DataLoaded(val data: AccountInfo) : SettingsState()
    data class Error(val exception: Throwable) : SettingsState()
}