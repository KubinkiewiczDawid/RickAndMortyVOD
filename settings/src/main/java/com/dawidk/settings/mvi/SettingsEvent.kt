package com.dawidk.settings.mvi

import com.dawidk.common.mvi.ViewEvent

sealed class SettingsEvent: ViewEvent {
    object NavigateToSignInScreen : SettingsEvent()
}