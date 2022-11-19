package com.dawidk.rickandmortyvod.navigation

import android.content.Context
import android.net.Uri
import androidx.navigation.NavController
import com.dawidk.common.navigation.Navigator
import com.dawidk.common.navigation.NavigatorScreen
import com.dawidk.common.navigation.SettingsActivityNavigator

class MainNavigator(
    context: Context,
    private val settingsActivityNavigator: SettingsActivityNavigator
) : Navigator {

    override var navController: NavController? = null
    private val _navController: NavController
        get() = navController!!
    private val resource = context.resources

    override fun navigateTo(screen: NavigatorScreen) {
        when (screen) {
            is Screen.HomeScreen -> goToHomeScreen(screen)
            is Screen.SettingsScreen -> goToSettingsScreen()
        }
    }

    private fun goToSettingsScreen() {
        settingsActivityNavigator.startSettingsActivity()
    }

    private fun goToHomeScreen(screen: Screen.HomeScreen) {
        _navController.navigate(Uri.parse("https://www.rickandmortyvod.dawidk.com/homeScreen${screen.accountInfo}"))
    }
}