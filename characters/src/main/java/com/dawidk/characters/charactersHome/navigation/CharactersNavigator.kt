package com.dawidk.characters.charactersHome.navigation

import androidx.navigation.NavController
import com.dawidk.characters.R
import com.dawidk.common.NavigationDirections
import com.dawidk.common.navigation.Navigator
import com.dawidk.common.navigation.NavigatorScreen

class CharactersNavigator(
) : Navigator {

    override var navController: NavController? = null
    private val _navController: NavController
        get() = navController!!

    override fun navigateTo(screen: NavigatorScreen) {
        when (screen) {
            is Screen.CharacterDetails -> goToCharacterDetailsScreen(screen)
        }
    }

    private fun goToCharacterDetailsScreen(screen: Screen.CharacterDetails) {
        if (_navController.currentDestination?.id == R.id.charactersFragment) {
            _navController.navigate(
                NavigationDirections.actionFragmentContainerToCharacterDetailsFragment(
                    screen.id
                )
            )
        }
    }
}