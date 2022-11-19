package com.dawidk.episodes.episodeDetails.state

sealed class EpisodeDetailsAction {
    data class GetEpisodeById(val id: String) : EpisodeDetailsAction()
    data class NavigateToCharacterDetailsScreen(val id: String) : EpisodeDetailsAction()
    data class NavigateToVideoPlayerScreen(val id: String) : EpisodeDetailsAction()
}