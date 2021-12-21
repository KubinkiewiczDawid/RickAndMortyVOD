package com.dawidk.episodes.episodeDetails.state

import com.dawidk.episodes.model.EpisodeItem

sealed class EpisodeDetailsState {
    data class GetEpisodeSuccess(val episodeItems: List<EpisodeItem>) : EpisodeDetailsState()
    data class Error(val exception: Throwable, val characterId: String) : EpisodeDetailsState()
    object Loading : EpisodeDetailsState()
}