package com.dawidk.episodes.mvi

import com.dawidk.common.mvi.ViewEvent

sealed class EpisodeEvent: ViewEvent {
    data class NavigateToEpisodeDetails(val id: String) : EpisodeEvent()
    data class NavigateToSeasonsList(val selectedSeason: String, val seasonsList: List<String>) : EpisodeEvent()
    data class NavigateToVideoPlayerScreen(val episodeId: String) : EpisodeEvent()
}