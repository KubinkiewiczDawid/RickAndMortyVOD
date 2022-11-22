package com.dawidk.characters.characterDetails

import androidx.lifecycle.viewModelScope
import com.dawidk.characters.characterDetails.mvi.CharacterDetailsAction
import com.dawidk.characters.characterDetails.mvi.CharacterDetailsEvent
import com.dawidk.characters.characterDetails.mvi.CharacterDetailsState
import com.dawidk.common.mvi.BaseViewModel
import com.dawidk.core.executors.FetchCharacterByIdExecutor
import com.dawidk.core.utils.DataLoadingException
import kotlinx.coroutines.launch

class CharacterDetailsViewModel(
    private val fetchCharacterByIdExecutor: FetchCharacterByIdExecutor,
    private val characterDetailsItemsProvider: CharacterDetailsItemsProvider
) : BaseViewModel<CharacterDetailsEvent, CharacterDetailsAction, CharacterDetailsState>(
    CharacterDetailsState.Loading
) {

    override fun onAction(action: CharacterDetailsAction) {
        when (action) {
            is CharacterDetailsAction.GetCharacterById -> fetchCharacterById(action.id)
            is CharacterDetailsAction.NavigateToEpisodeDetailsScreen -> {
                navigateToEpisodeDetails(action.episodeId)
            }
            is CharacterDetailsAction.GetEpisodes -> {
                updateState(CharacterDetailsState.GetEpisodes)
            }
            is CharacterDetailsAction.GetLocations -> {
                updateState(CharacterDetailsState.GetLocations)
            }
        }
    }

    private fun fetchCharacterById(id: String) {
        updateState(CharacterDetailsState.Loading)
        viewModelScope.launch {
            try {
                val character = fetchCharacterByIdExecutor.getCharacterById(id)
                val characterItemsList = characterDetailsItemsProvider(character)
                updateState(CharacterDetailsState.GetCharacterSuccess(characterItemsList))
            } catch (ex: DataLoadingException) {
                updateState(
                    CharacterDetailsState.Error(
                        DataLoadingException("Cannot fetch character"),
                        id
                    )
                )
            }
        }
    }

    private fun navigateToEpisodeDetails(episodeId: String) {
        emitEvent(CharacterDetailsEvent.NavigateToEpisodeDetails(episodeId))
    }
}