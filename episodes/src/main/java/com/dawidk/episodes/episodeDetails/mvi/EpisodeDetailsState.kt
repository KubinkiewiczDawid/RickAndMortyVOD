package com.dawidk.episodes.episodeDetails.mvi

import com.dawidk.common.mvi.ViewState
import com.dawidk.episodes.model.EpisodeItem

sealed class EpisodeDetailsState: ViewState {
    data class GetEpisodeSuccess(val episodeItems: List<EpisodeItem>) : EpisodeDetailsState()
    data class Error(val exception: Throwable, val characterId: String) : EpisodeDetailsState()
    object Loading : EpisodeDetailsState()
}