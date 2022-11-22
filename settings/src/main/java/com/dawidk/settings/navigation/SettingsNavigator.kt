package com.dawidk.settings.navigation

import androidx.navigation.NavController
import com.dawidk.common.navigation.Navigator
import com.dawidk.common.navigation.NavigatorScreen
import com.dawidk.common.navigation.RegistrationActivityNavigator
import com.dawidk.settings.navigation.Screen.SignInScreen

class SettingsNavigator(
    private val registrationActivityNavigator: RegistrationActivityNavigator
) : Navigator {

    override var navController: NavController? = null
    private val _navController: NavController
        get() = navController!!

    override fun navigateTo(screen: NavigatorScreen) {
        when (screen) {
            is SignInScreen -> goToSignInScreen()
        }
    }

    private fun goToSignInScreen() {
        registrationActivityNavigator.startRegistrationActivity()
    }
}