package com.dawidk.characters.charactersHome

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dawidk.characters.charactersHome.mvi.CharacterAction
import com.dawidk.characters.charactersHome.mvi.CharacterEvent
import com.dawidk.characters.charactersHome.mvi.CharacterState
import com.dawidk.common.mvi.BaseViewModel
import com.dawidk.core.CharacterSource
import com.dawidk.core.datastore.HomeScreenDataStoreRepository
import com.dawidk.core.domain.model.Character
import com.dawidk.core.executors.FetchCharactersListExecutor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

class CharacterHomeViewModel(
    private val fetchCharactersListExecutor: FetchCharactersListExecutor,
    private val homeScreenDataStoreRepository: HomeScreenDataStoreRepository
) : BaseViewModel<CharacterEvent, CharacterAction, CharacterState>(CharacterState.Loading) {

    private var pagingData: PagingData<Character> = PagingData.empty()

    override fun onAction(action: CharacterAction) {
        when (action) {
            is CharacterAction.NavigateToDetailsScreen -> navigateToCharacterDetails(action.id)
            is CharacterAction.Init -> fetchCharactersList()
            is CharacterAction.Load -> updateState(CharacterState.Loading)
            is CharacterAction.DataLoaded -> {
                if (state.value is CharacterState.Loading) {
                    updateState(CharacterState.DataLoaded(pagingData))
                }
            }
            is CharacterAction.HandleError -> updateState(CharacterState.Error(action.error))
        }
    }

    private fun fetchCharactersList() {
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                CharacterSource(fetchCharactersListExecutor)
            }.flow
                .cachedIn(viewModelScope).collectLatest {
                    pagingData = it
                    updateState(CharacterState.DataLoaded(it))
                }
        }
    }

    private fun navigateToCharacterDetails(id: String) {
        viewModelScope.launch {
            homeScreenDataStoreRepository.updateLastSeenCharacter(id)
            emitEvent(CharacterEvent.NavigateToCharacterDetails(id))
        }
    }
}