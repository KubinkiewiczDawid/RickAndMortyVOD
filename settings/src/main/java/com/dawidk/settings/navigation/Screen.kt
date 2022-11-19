package com.dawidk.settings.navigation

import com.dawidk.common.navigation.NavigatorScreen

sealed class Screen : NavigatorScreen {
    object SignInScreen : Screen()
}