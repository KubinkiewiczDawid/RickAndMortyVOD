package com.dawidk.characters.charactersHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dawidk.core.CharacterSource
import com.dawidk.core.datastore.HomeScreenDataStoreRepository
import com.dawidk.core.domain.model.Character
import com.dawidk.core.executors.FetchCharactersListExecutor
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

class CharacterHomeViewModel(
    private val fetchCharactersListExecutor: FetchCharactersListExecutor,
    private val homeScreenDataStoreRepository: HomeScreenDataStoreRepository
) : ViewModel() {

    private val _state: MutableStateFlow<CharacterState> = MutableStateFlow(CharacterState.Loading)
    val state: StateFlow<CharacterState> = _state
    private val _event: MutableSharedFlow<CharacterEvent> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<CharacterEvent> = _event
    private var pagingData: PagingData<Character> = PagingData.empty()

    fun onAction(action: CharacterAction) {
        when (action) {
            is CharacterAction.NavigateToDetailsScreen -> navigateToCharacterDetails(action.id)
            is CharacterAction.Init -> fetchCharactersList()
            is CharacterAction.Load -> _state.value = CharacterState.Loading
            is CharacterAction.DataLoaded -> {
                if (_state.value is CharacterState.Loading) {
                    _state.value = CharacterState.DataLoaded(pagingData)
                }
            }
            is CharacterAction.HandleError -> _state.value = CharacterState.Error(action.error)
        }
    }

    private fun fetchCharactersList() {
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                CharacterSource(fetchCharactersListExecutor)
            }.flow
                .cachedIn(viewModelScope).collectLatest {
                    pagingData = it
                    _state.value = CharacterState.DataLoaded(it)
                }
        }
    }

    private fun navigateToCharacterDetails(id: String) {
        viewModelScope.launch {
            homeScreenDataStoreRepository.updateLastSeenCharacter(id)
            _event.tryEmit(CharacterEvent.NavigateToCharacterDetails(id))
        }
    }
}