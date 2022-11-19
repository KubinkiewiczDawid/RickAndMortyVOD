package com.dawidk.episodes.state

sealed class EpisodeEvent {
    data class NavigateToEpisodeDetails(val id: String) : EpisodeEvent()
    data class NavigateToSeasonsList(val selectedSeason: String, val seasonsList: List<String>) : EpisodeEvent()
    data class NavigateToVideoPlayerScreen(val episodeId: String) : EpisodeEvent()
}