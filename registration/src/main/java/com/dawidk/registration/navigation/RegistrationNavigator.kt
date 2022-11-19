package com.dawidk.registration.navigation

import android.content.Context
import androidx.navigation.NavController
import com.dawidk.common.navigation.MainActivityNavigator
import com.dawidk.common.navigation.Navigator
import com.dawidk.common.navigation.NavigatorScreen
import com.dawidk.registration.R

class RegistrationNavigator(
    context: Context,
    private val mainActivityNavigator: MainActivityNavigator
) : Navigator {

    override var navController: NavController? = null
    private val _navController: NavController
        get() = navController!!
    private val resource = context.resources

    override fun navigateTo(screen: NavigatorScreen) {
        when (screen) {
            is Screen.SignUpScreen -> goToSignUpScreen()
            is Screen.SignInScreen -> goToSignInScreen()
            is Screen.HomeScreen -> goToHomeScreen()
        }
    }

    private fun goToSignUpScreen() {
        _navController.navigate(R.id.action_signIn_to_signUp_fragment)
    }

    private fun goToSignInScreen() {
        _navController.popBackStack()
    }

    private fun goToHomeScreen() {
        mainActivityNavigator.startMainActivity()
    }
}