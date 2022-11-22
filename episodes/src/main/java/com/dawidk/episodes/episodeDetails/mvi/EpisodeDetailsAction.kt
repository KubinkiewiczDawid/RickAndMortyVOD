package com.dawidk.episodes.episodeDetails.mvi

import com.dawidk.common.mvi.ViewAction

sealed class EpisodeDetailsAction: ViewAction {
    data class GetEpisodeById(val id: String) : EpisodeDetailsAction()
    data class NavigateToCharacterDetailsScreen(val id: String) : EpisodeDetailsAction()
    data class NavigateToVideoPlayerScreen(val id: String) : EpisodeDetailsAction()
}