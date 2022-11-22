package com.dawidk.search

import androidx.lifecycle.viewModelScope
import com.dawidk.common.mvi.BaseViewModel
import com.dawidk.search.model.SearchItem
import com.dawidk.search.mvi.SearchAction
import com.dawidk.search.mvi.SearchEvent
import com.dawidk.search.mvi.SearchState
import com.dawidk.search.usecase.MIN_QUERY_LENGTH
import com.dawidk.search.usecase.SearchItemsUseCase
import kotlinx.coroutines.launch

class SearchViewModel(
    val searchItemsUseCase: SearchItemsUseCase
) : BaseViewModel<SearchEvent, SearchAction, SearchState>(SearchState.DataLoaded(emptyList())) {

    private var foundItems: List<SearchItem> = emptyList()

    override fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.Search -> searchItems(action.name)
            is SearchAction.Load -> updateState(SearchState.Loading)
            is SearchAction.DataLoaded -> {
                if (state.value is SearchState.Loading) {
                    updateState(SearchState.DataLoaded(foundItems))
                }
            }
            is SearchAction.HandleError -> updateState(SearchState.Error(action.error))
            is SearchAction.NavigateToCharacterDetailsScreen -> navigateToCharacterDetailsScreen(
                action.id
            )
            is SearchAction.NavigateToEpisodeDetailsScreen -> navigateToEpisodeDetailsScreen(action.id)
        }
    }

    private fun searchItems(searchQuery: String) {
        viewModelScope.launch {
            if (searchQuery.length > MIN_QUERY_LENGTH) {
                updateState(SearchState.Loading)
            }

            searchItemsUseCase(searchQuery).collect { searchResults ->
                foundItems = searchResults
                updateState(SearchState.DataLoaded(foundItems))
            }
        }
    }

    private fun navigateToCharacterDetailsScreen(id: String) {
        viewModelScope.launch {
            emitEvent(SearchEvent.NavigateToCharacterDetailsScreen(id))
        }
    }

    private fun navigateToEpisodeDetailsScreen(id: String) {
        viewModelScope.launch {
            emitEvent(SearchEvent.NavigateToEpisodeDetailsScreen(id))
        }
    }
}