package com.dawidk.episodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawidk.core.domain.model.EpisodeFilter
import com.dawidk.episodes.model.EpisodesData
import com.dawidk.episodes.state.EpisodeAction
import com.dawidk.episodes.state.EpisodeEvent
import com.dawidk.episodes.state.EpisodeState
import com.dawidk.episodes.usecase.FetchEpisodesListUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EpisodesViewModel(
    private val fetchEpisodesListUseCase: FetchEpisodesListUseCase
) :
    ViewModel() {

    private val _state: MutableStateFlow<EpisodeState> = MutableStateFlow(EpisodeState.Loading)
    val state: StateFlow<EpisodeState> = _state
    private val _event: MutableSharedFlow<EpisodeEvent> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<EpisodeEvent> = _event
    private var episodesData = EpisodesData.DEFAULT
    private var filteredEpisodesData = EpisodesData.DEFAULT

    fun onAction(action: EpisodeAction) {
        when (action) {
            is EpisodeAction.Init -> {
                fetchEpisodesList()
            }
            is EpisodeAction.Update -> updateEpisodesList(action.episodeFilter)
            is EpisodeAction.Load -> _state.value = EpisodeState.Loading
            is EpisodeAction.DataLoaded -> {
                if (_state.value is EpisodeState.Loading) {
                    _state.value = EpisodeState.DataLoaded(episodesData)
                }
            }
            is EpisodeAction.HandleError -> _state.value =
                EpisodeState.Error(action.error)
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
        _state.value = EpisodeState.DataLoaded(filteredEpisodesData)
    }

    private fun fetchEpisodesList() {
        viewModelScope.launch {
            fetchEpisodesListUseCase().collect {
                episodesData = EpisodesData(episodesMap = it)
                _state.value = EpisodeState.DataLoaded(episodesData)
            }
        }
    }

    private fun navigateToSeasonsList() {
        viewModelScope.launch {
            _event.tryEmit(
                EpisodeEvent.NavigateToSeasonsList(
                    filteredEpisodesData.chosenSeason,
                    episodesData.episodesMap.keys.toList()
                )
            )
        }
    }

    private fun navigateToEpisodeDetails(id: String) {
        viewModelScope.launch {
            _event.tryEmit(EpisodeEvent.NavigateToEpisodeDetails(id))
        }
    }

    private fun navigateToVideoPlayerScreen(episodeId: String) {
        viewModelScope.launch {
            _event.tryEmit(EpisodeEvent.NavigateToVideoPlayerScreen(episodeId))
        }
    }

}