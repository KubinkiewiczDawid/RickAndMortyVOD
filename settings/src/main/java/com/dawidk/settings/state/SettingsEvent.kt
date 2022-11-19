package com.dawidk.settings.state

sealed class SettingsEvent {
    object NavigateToSignInScreen : SettingsEvent()
}