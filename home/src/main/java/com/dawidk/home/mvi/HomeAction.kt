package com.dawidk.home.mvi

import com.dawidk.common.mvi.ViewAction
import com.dawidk.home.model.Playlist

sealed class HomeAction : ViewAction {
    object LoadHomeItems : HomeAction()
    data class NavigateToSeeAllScreen(val playlist: Playlist) : HomeAction()
    data class NavigateToCharacterDetailsScreen(val id: String) : HomeAction()
    data class NavigateToEpisodeDetailsScreen(val id: String) : HomeAction()
    data class NavigateToVideoPlayerScreen(val episodeId: String) : HomeAction()
}