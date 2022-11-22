package com.dawidk.home.mvi

import com.dawidk.common.mvi.ViewEvent
import com.dawidk.home.model.Playlist

sealed class HomeEvent : ViewEvent {
    data class NavigateToSeeAllScreen(val playlist: Playlist) : HomeEvent()
    data class NavigateToCharacterDetailsScreen(val id: String) : HomeEvent()
    data class NavigateToEpisodeDetailsScreen(val id: String) : HomeEvent()
    data class NavigateToVideoPlayerScreen(val episodeId: String) : HomeEvent()
}