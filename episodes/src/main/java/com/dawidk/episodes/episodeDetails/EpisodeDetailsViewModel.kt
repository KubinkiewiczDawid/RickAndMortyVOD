package com.dawidk.episodes.episodeDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawidk.core.executors.FetchEpisodeByIdExecutor
import com.dawidk.core.firestore.FirestoreClientApi
import com.dawidk.core.utils.DataLoadingException
import com.dawidk.episodes.episodeDetails.state.EpisodeDetailsAction
import com.dawidk.episodes.episodeDetails.state.EpisodeDetailsEvent
import com.dawidk.episodes.episodeDetails.state.EpisodeDetailsState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EpisodeDetailsViewModel(
    private val fetchEpisodeByIdExecutor: FetchEpisodeByIdExecutor,
    private val firestoreClient: FirestoreClientApi,
    private val episodeDetailsItemsProvider: EpisodeDetailsItemsProvider
) : ViewModel() {

    private val _state: MutableStateFlow<EpisodeDetailsState> =
        MutableStateFlow(EpisodeDetailsState.Loading)
    val state: StateFlow<EpisodeDetailsState> = _state
    private val _event: MutableSharedFlow<EpisodeDetailsEvent> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<EpisodeDetailsEvent> = _event

    fun onAction(action: EpisodeDetailsAction) {
        when (action) {
            is EpisodeDetailsAction.GetEpisodeById -> fetchEpisodeById(action.id)
            is EpisodeDetailsAction.NavigateToCharacterDetailsScreen -> navigateToCharacterDetails(
                action.id
            )
            is EpisodeDetailsAction.NavigateToVideoPlayerScreen ->
                navigateToVideoPlayerScreen(action.id)
        }
    }

    private fun fetchEpisodeById(id: String) {
        _state.value = EpisodeDetailsState.Loading

        viewModelScope.launch {
            try {
                firestoreClient.updateLastSeenPlaylist(id)
                val episode = fetchEpisodeByIdExecutor.getEpisodeById(id)
                val episodeItemsList = episodeDetailsItemsProvider(episode)
                _state.value = EpisodeDetailsState.GetEpisodeSuccess(episodeItemsList)
            } catch (ex: DataLoadingException) {
                _state.value =
                    EpisodeDetailsState.Error(DataLoadingException("Cannot fetch episode"), id)
            }
        }
    }

    private fun navigateToCharacterDetails(id: String) {
        viewModelScope.launch {
            _event.tryEmit(EpisodeDetailsEvent.NavigateToCharacterDetails(id))
        }
    }

    private fun navigateToVideoPlayerScreen(id: String) {
        viewModelScope.launch {
            _event.tryEmit(EpisodeDetailsEvent.NavigateToVideoPlayerScreen(id))
        }
    }
}