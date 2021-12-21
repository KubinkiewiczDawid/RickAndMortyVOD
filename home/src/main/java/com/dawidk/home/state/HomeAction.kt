package com.dawidk.home.state

import com.dawidk.home.model.Playlist

sealed class HomeAction {
    object LoadHomeItems : HomeAction()
    data class NavigateToSeeAllScreen(val playlist: Playlist) : HomeAction()
    data class NavigateToCharacterDetailsScreen(val id: String) : HomeAction()
    data class NavigateToEpisodeDetailsScreen(val id: String) : HomeAction()
    data class NavigateToVideoPlayerScreen(val episodeId: String) : HomeAction()
}