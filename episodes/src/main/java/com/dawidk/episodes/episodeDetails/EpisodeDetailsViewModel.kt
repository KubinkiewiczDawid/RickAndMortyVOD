package com.dawidk.episodes.episodeDetails

import androidx.lifecycle.viewModelScope
import com.dawidk.common.mvi.BaseViewModel
import com.dawidk.core.executors.FetchEpisodeByIdExecutor
import com.dawidk.core.firestore.FirestoreClientApi
import com.dawidk.core.utils.DataLoadingException
import com.dawidk.episodes.episodeDetails.mvi.EpisodeDetailsAction
import com.dawidk.episodes.episodeDetails.mvi.EpisodeDetailsEvent
import com.dawidk.episodes.episodeDetails.mvi.EpisodeDetailsState
import kotlinx.coroutines.launch

class EpisodeDetailsViewModel(
    private val fetchEpisodeByIdExecutor: FetchEpisodeByIdExecutor,
    private val firestoreClient: FirestoreClientApi,
    private val episodeDetailsItemsProvider: EpisodeDetailsItemsProvider
) : BaseViewModel<EpisodeDetailsEvent, EpisodeDetailsAction, EpisodeDetailsState>(EpisodeDetailsState.Loading) {

    override fun onAction(action: EpisodeDetailsAction) {
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
        updateState(EpisodeDetailsState.Loading)

        viewModelScope.launch {
            try {
                firestoreClient.updateLastSeenPlaylist(id)
                val episode = fetchEpisodeByIdExecutor.getEpisodeById(id)
                val episodeItemsList = episodeDetailsItemsProvider(episode)
                updateState(EpisodeDetailsState.GetEpisodeSuccess(episodeItemsList))
            } catch (ex: DataLoadingException) {
                updateState(EpisodeDetailsState.Error(DataLoadingException("Cannot fetch episode"), id))
            }
        }
    }

    private fun navigateToCharacterDetails(id: String) {
        viewModelScope.launch {
            emitEvent(EpisodeDetailsEvent.NavigateToCharacterDetails(id))
        }
    }

    private fun navigateToVideoPlayerScreen(id: String) {
        viewModelScope.launch {
            emitEvent(EpisodeDetailsEvent.NavigateToVideoPlayerScreen(id))
        }
    }
}