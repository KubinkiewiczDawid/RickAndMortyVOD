package com.dawidk.characters.characterDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawidk.characters.characterDetails.state.CharacterDetailsAction
import com.dawidk.characters.characterDetails.state.CharacterDetailsEvent
import com.dawidk.core.executors.FetchCharacterByIdExecutor
import com.dawidk.core.utils.DataLoadingException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CharacterDetailsViewModel(
    private val fetchCharacterByIdExecutor: FetchCharacterByIdExecutor,
    private val characterDetailsItemsProvider: CharacterDetailsItemsProvider
) : ViewModel() {

    private val _state: MutableStateFlow<CharacterDetailsState> =
        MutableStateFlow(CharacterDetailsState.Loading)
    val state: StateFlow<CharacterDetailsState> = _state
    private val _event: MutableSharedFlow<CharacterDetailsEvent> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<CharacterDetailsEvent> = _event

    fun onAction(action: CharacterDetailsAction) {
        when (action) {
            is CharacterDetailsAction.GetCharacterById -> fetchCharacterById(action.id)
            is CharacterDetailsAction.NavigateToEpisodeDetailsScreen -> {
                navigateToEpisodeDetails(action.episodeId)
            }
            is CharacterDetailsAction.GetEpisodes -> {
                _state.value = CharacterDetailsState.GetEpisodes
            }
            is CharacterDetailsAction.GetLocations -> {
                _state.value = CharacterDetailsState.GetLocations
            }
        }
    }

    private fun fetchCharacterById(id: String) {
        _state.value = CharacterDetailsState.Loading
        viewModelScope.launch {
            try {
                val character = fetchCharacterByIdExecutor.getCharacterById(id)
                val characterItemsList = characterDetailsItemsProvider(character)
                _state.value = CharacterDetailsState.GetCharacterSuccess(characterItemsList)
            } catch (ex: DataLoadingException) {
                _state.value =
                    CharacterDetailsState.Error(DataLoadingException("Cannot fetch character"), id)
            }
        }
    }

    private fun navigateToEpisodeDetails(episodeId: String) {
        _event.tryEmit(CharacterDetailsEvent.NavigateToEpisodeDetails(episodeId))
    }
}