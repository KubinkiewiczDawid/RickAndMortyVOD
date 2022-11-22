package com.dawidk.search.navigation

import androidx.navigation.NavController
import com.dawidk.common.NavigationDirections
import com.dawidk.common.navigation.Navigator
import com.dawidk.common.navigation.NavigatorScreen

class SearchNavigator: Navigator {

    override var navController: NavController? = null
    private val _navController: NavController
        get() = navController!!

    override fun navigateTo(screen: NavigatorScreen) {
        when (screen) {
            is Screen.CharacterDetails -> goToCharacterDetailsScreen(screen)
            is Screen.EpisodeDetails -> goToEpisodeDetailsScreen(screen)
        }
    }

    private fun goToCharacterDetailsScreen(screen: Screen.CharacterDetails) {
        _navController.navigate(
            NavigationDirections.actionFragmentContainerToCharacterDetailsFragment(
                screen.id
            )
        )
    }

    private fun goToEpisodeDetailsScreen(screen: Screen.EpisodeDetails) {
        _navController.navigate(
            NavigationDirections.actionFragmentContainerToEpisodeDetailsFragment(
                screen.id
            )
        )
    }
}

sealed class Screen : NavigatorScreen {
    data class CharacterDetails(val id: String) : Screen()
    data class EpisodeDetails(val id: String) : Screen()
}