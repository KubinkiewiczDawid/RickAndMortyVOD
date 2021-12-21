package com.dawidk.characters.navigation

import android.content.Context
import androidx.navigation.NavController
import com.dawidk.characters.R
import com.dawidk.common.NavigationDirections
import com.dawidk.common.navigation.Navigator
import com.dawidk.common.navigation.NavigatorScreen

class CharactersNavigator(
    context: Context
) : Navigator {

    override var navController: NavController? = null
    private val _navController: NavController
        get() = navController!!
    private val resource = context.resources

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