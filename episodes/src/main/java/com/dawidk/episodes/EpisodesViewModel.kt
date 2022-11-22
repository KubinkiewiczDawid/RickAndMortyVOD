package com.dawidk.episodes

import androidx.lifecycle.viewModelScope
import com.dawidk.common.mvi.BaseViewModel
import com.dawidk.core.domain.model.EpisodeFilter
import com.dawidk.episodes.model.EpisodesData
import com.dawidk.episodes.mvi.EpisodeAction
import com.dawidk.episodes.mvi.EpisodeEvent
import com.dawidk.episodes.mvi.EpisodeState
import com.dawidk.episodes.usecase.FetchEpisodesListUseCase
import kotlinx.coroutines.launch

class EpisodesViewModel(
    private val fetchEpisodesListUseCase: FetchEpisodesListUseCase
) : BaseViewModel<EpisodeEvent, EpisodeAction, EpisodeState>(EpisodeState.Loading) {

    private var episodesData = EpisodesData.DEFAULT
    private var filteredEpisodesData = EpisodesData.DEFAULT

    override fun onAction(action: EpisodeAction) {
        when (action) {
            is EpisodeAction.Init -> {
                fetchEpisodesList()
            }
            is EpisodeAction.Update -> updateEpisodesList(action.episodeFilter)
            is EpisodeAction.Load -> updateState(EpisodeState.Loading)
            is EpisodeAction.DataLoaded -> {
                if (state.value is EpisodeState.Loading) {
                    updateState(EpisodeState.DataLoaded(episodesData))
                }
            }
            is EpisodeAction.HandleError -> updateState(EpisodeState.Error(action.error))
            is EpisodeAction.NavigateToEpisodeDetailsScreen -> navigateToEpisodeDetails(action.id)
            is EpisodeAction.NavigateToSeasonsList -> {
                navigateToSeasonsList()
            }
            is EpisodeAction.NavigateToVideoPlayerScreen -> navigateToVideoPlayerScreen(action.episodeId)
        }
    }

    private fun updateEpisodesList(episodeFilter: EpisodeFilter) {
        filteredEpisodesData =
            EpisodesData(episodeFilter.episode, episodesData.episodesMap.toMutableMap())
        updateState(EpisodeState.DataLoaded(filteredEpisodesData))
    }

    private fun fetchEpisodesList() {
        viewModelScope.launch {
            fetchEpisodesListUseCase().collect {
                episodesData = EpisodesData(episodesMap = it)
                updateState(EpisodeState.DataLoaded(episodesData))
            }
        }
    }

    private fun navigateToSeasonsList() {
        viewModelScope.launch {
            emitEvent(
                EpisodeEvent.NavigateToSeasonsList(
                    filteredEpisodesData.chosenSeason,
                    episodesData.episodesMap.keys.toList()
                )
            )
        }
    }

    private fun navigateToEpisodeDetails(id: String) {
        viewModelScope.launch {
            emitEvent(EpisodeEvent.NavigateToEpisodeDetails(id))
        }
    }

    private fun navigateToVideoPlayerScreen(episodeId: String) {
        viewModelScope.launch {
            emitEvent(EpisodeEvent.NavigateToVideoPlayerScreen(episodeId))
        }
    }

}