package com.dawidk.episodes.mvi

import com.dawidk.common.mvi.ViewState
import com.dawidk.episodes.model.EpisodesData

sealed class EpisodeState: ViewState {
    object Loading : EpisodeState()
    data class DataLoaded(val data: EpisodesData) : EpisodeState()
    data class Error(val exception: Throwable) : EpisodeState()
}