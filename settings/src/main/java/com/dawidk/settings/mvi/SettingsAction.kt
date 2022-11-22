package com.dawidk.settings.mvi

import com.dawidk.common.mvi.ViewAction
import com.dawidk.core.domain.model.AccountInfo

sealed class SettingsAction: ViewAction {
    object Init : SettingsAction()
    object Load : SettingsAction()
    data class DataLoaded(val data: AccountInfo) : SettingsAction()
    object NavigateToSignInScreen : SettingsAction()
}