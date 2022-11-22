package com.dawidk.home.navigation

import androidx.navigation.NavController
import com.dawidk.common.NavigationDirections
import com.dawidk.common.navigation.Navigator
import com.dawidk.common.navigation.NavigatorScreen
import com.dawidk.common.navigation.VideoPlayerActivityNavigator
import com.dawidk.common.video.VideoType
import com.dawidk.home.HomeFragmentDirections

class HomeNavigator(
    private val videoPlayerActivityNavigator: VideoPlayerActivityNavigator
) : Navigator {

    override var navController: NavController? = null
    private val _navController: NavController
        get() = navController!!

    override fun navigateTo(screen: NavigatorScreen) {
        when (screen) {
            is Screen.SeeAll -> goToSeeAllScreen(screen)
            is Screen.CharacterDetails -> goToCharacterDetailsScreen(screen)
            is Screen.EpisodeDetails -> goToEpisodeDetailsScreen(screen)
            is Screen.VideoPlayer -> goToVideoPlayerScreen(screen)
        }
    }

    private fun goToSeeAllScreen(screen: Screen.SeeAll) {
        val action =
            HomeFragmentDirections.actionHomeFragmentToSeeAllFragment(screen.playlist)
        _navController.navigate(
            action
        )
    }

    private fun goToCharacterDetailsScreen(screen: Screen.CharacterDetails) {
        _navController.navigate(
            NavigationDirections.actionFragmentContainerToCharacterDetailsFragment(
                screen.id
            )
        )
    }

    private fun goToEpisodeDetailsScreen(screen: Screen.EpisodeDetails) {
        _navController.navigate(NavigationDirections.actionFragmentContainerToEpisodeDetailsFragment(screen.id))
    }

    private fun goToVideoPlayerScreen(screen: Screen.VideoPlayer) {
        videoPlayerActivityNavigator.startVideoPlayerActivity(screen.episodeId, VideoType.EPISODE)
    }
}