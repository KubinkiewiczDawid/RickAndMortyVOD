package com.dawidk.episodes.episodeDetails.mvi

import com.dawidk.common.mvi.ViewEvent

sealed class EpisodeDetailsEvent: ViewEvent {
    data class NavigateToCharacterDetails(val id: String) : EpisodeDetailsEvent()
    data class NavigateToVideoPlayerScreen(val id: String) : EpisodeDetailsEvent()
}