package com.dawidk.home.state

import com.dawidk.home.model.Playlist

sealed class HomeEvent {
    data class NavigateToSeeAllScreen(val playlist: Playlist) : HomeEvent()
    data class NavigateToCharacterDetailsScreen(val id: String) : HomeEvent()
    data class NavigateToEpisodeDetailsScreen(val id: String) : HomeEvent()
    data class NavigateToVideoPlayerScreen(val episodeId: String) : HomeEvent()
}