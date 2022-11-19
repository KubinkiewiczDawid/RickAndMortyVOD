package com.dawidk.episodes.episodeDetails.state

sealed class EpisodeDetailsEvent {
    data class NavigateToCharacterDetails(val id: String) : EpisodeDetailsEvent()
    data class NavigateToVideoPlayerScreen(val id: String) : EpisodeDetailsEvent()
}