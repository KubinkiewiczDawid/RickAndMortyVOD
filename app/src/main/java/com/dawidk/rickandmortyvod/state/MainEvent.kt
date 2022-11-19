package com.dawidk.rickandmortyvod.state

sealed class MainEvent {
    object NavigateToSettingsScreen : MainEvent()
}