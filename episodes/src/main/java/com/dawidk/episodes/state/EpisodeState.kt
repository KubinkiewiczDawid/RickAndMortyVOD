package com.dawidk.episodes.state

import com.dawidk.episodes.model.EpisodesData

sealed class EpisodeState {
    object Loading : EpisodeState()
    data class DataLoaded(val data: EpisodesData) : EpisodeState()
    data class Error(val exception: Throwable) : EpisodeState()
}