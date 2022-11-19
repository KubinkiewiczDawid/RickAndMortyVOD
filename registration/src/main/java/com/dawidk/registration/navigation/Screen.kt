package com.dawidk.registration.navigation

import com.dawidk.common.navigation.NavigatorScreen

sealed class Screen : NavigatorScreen {
    object SignUpScreen : Screen()
    object SignInScreen : Screen()
    object HomeScreen : Screen()
}