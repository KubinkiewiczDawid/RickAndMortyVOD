package com.dawidk.characters.characterDetails.navigation

import android.content.Context
import androidx.navigation.NavController
import com.dawidk.common.NavigationDirections
import com.dawidk.common.navigation.Navigator
import com.dawidk.common.navigation.NavigatorScreen

class CharacterDetailsNavigator(
    context: Context
) : Navigator {

    override var navController: NavController? = null
    private val _navController: NavController
        get() = navController!!
    private val resource = context.resources

    override fun navigateTo(screen: NavigatorScreen) {
        when (screen) {
            is Screen.EpisodeDetails -> goToEpisodeDetailsScreen(screen)
        }
    }

    private fun goToEpisodeDetailsScreen(screen: Screen.EpisodeDetails) {
        _navController.navigate(
            NavigationDirections.actionFragmentContainerToEpisodeDetailsFragment(
                screen.id
            )
        )
    }
}