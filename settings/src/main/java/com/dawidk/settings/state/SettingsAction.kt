package com.dawidk.settings.state

import com.dawidk.core.domain.model.AccountInfo

sealed class SettingsAction {
    object Init : SettingsAction()
    object Load : SettingsAction()
    data class DataLoaded(val data: AccountInfo) : SettingsAction()
    object NavigateToSignInScreen : SettingsAction()
}