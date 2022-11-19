package com.dawidk.episodes.episodeDetails.navigation

import android.content.Context
import androidx.navigation.NavController
import com.dawidk.common.NavigationDirections
import com.dawidk.common.navigation.Navigator
import com.dawidk.common.navigation.NavigatorScreen
import com.dawidk.common.navigation.VideoPlayerActivityNavigator
import com.dawidk.common.video.VideoType

class EpisodeDetailsNavigator(
    context: Context,
    private val videoPlayerActivityNavigator: VideoPlayerActivityNavigator
) : Navigator {

    override var navController: NavController? = null
    private val _navController: NavController
        get() = navController!!
    private val resource = context.resources

    override fun navigateTo(screen: NavigatorScreen) {
        when (screen) {
            is Screen.CharacterDetails -> goToCharacterDetailsScreen(screen)
            is Screen.VideoPlayer -> goToVideoPlayerScreen(screen)
        }
    }

    private fun goToCharacterDetailsScreen(screen: Screen.CharacterDetails) {
        _navController.navigate(
            NavigationDirections.actionFragmentContainerToCharacterDetailsFragment(
                screen.id
            )
        )
    }

    private fun goToVideoPlayerScreen(screen: Screen.VideoPlayer) {
        videoPlayerActivityNavigator.startVideoPlayerActivity(screen.id, VideoType.EPISODE)
    }
}