package com.dawidk.episodes.navigation

import android.net.Uri
import androidx.navigation.NavController
import com.dawidk.common.NavigationDirections
import com.dawidk.common.navigation.Navigator
import com.dawidk.common.navigation.NavigatorScreen
import com.dawidk.common.navigation.VideoPlayerActivityNavigator
import com.dawidk.common.video.VideoType
import com.dawidk.episodes.R

class EpisodesNavigator(
    private val videoPlayerActivityNavigator: VideoPlayerActivityNavigator
) : Navigator {

    override var navController: NavController? = null
    private val _navController: NavController
        get() = navController!!

    override fun navigateTo(screen: NavigatorScreen) {
        when (screen) {
            is Screen.EpisodeDetails -> goToEpisodeDetailsScreen(screen)
            is Screen.CharacterDetails -> goToCharacterDetailsScreen(screen)
            is Screen.VideoPlayer -> goToVideoPlayerScreen(screen)
        }
    }

    private fun goToEpisodeDetailsScreen(screen: Screen.EpisodeDetails) {
        val action =
            NavigationDirections.actionFragmentContainerToEpisodeDetailsFragment(screen.id)
        if (_navController.currentDestination?.id == R.id.episodesFragment) {
            _navController.navigate(
                action
            )
        }
    }

    private fun goToCharacterDetailsScreen(screen: Screen.CharacterDetails) {
        _navController.navigate(Uri.parse("https://www.rickandmortyvod.dawidk.com/characterDetails/${screen.id}"))
    }

    private fun goToVideoPlayerScreen(screen: Screen.VideoPlayer) {
        videoPlayerActivityNavigator.startVideoPlayerActivity(screen.id, VideoType.EPISODE)
    }
}