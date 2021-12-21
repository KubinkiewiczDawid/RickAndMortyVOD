package com.dawidk.rickandmortyvod.navigation

import com.dawidk.common.navigation.NavigatorScreen
import com.dawidk.core.domain.model.AccountInfo

sealed class Screen : NavigatorScreen {
    data class HomeScreen(val accountInfo: AccountInfo) : Screen()
    object SettingsScreen : Screen()
    object RegistrationScreen : Screen()
}