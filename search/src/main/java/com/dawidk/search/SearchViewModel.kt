package com.dawidk.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawidk.search.model.SearchItem
import com.dawidk.search.state.SearchAction
import com.dawidk.search.state.SearchEvent
import com.dawidk.search.state.SearchState
import com.dawidk.search.usecase.MIN_QUERY_LENGTH
import com.dawidk.search.usecase.SearchItemsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel(
    val searchItemsUseCase: SearchItemsUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<SearchState> =
        MutableStateFlow(SearchState.DataLoaded(emptyList()))
    val state: StateFlow<SearchState> = _state
    private val _event: MutableSharedFlow<SearchEvent> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<SearchEvent> = _event
    private var foundItems: List<SearchItem> = emptyList()

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.Search -> searchItems(action.name)
            is SearchAction.Load -> _state.value = SearchState.Loading
            is SearchAction.DataLoaded -> {
                if (_state.value is SearchState.Loading) {
                    _state.value = SearchState.DataLoaded(foundItems)
                }
            }
            is SearchAction.HandleError -> _state.value =
                SearchState.Error(action.error)
            is SearchAction.NavigateToCharacterDetailsScreen -> navigateToCharacterDetailsScreen(
                action.id
            )
            is SearchAction.NavigateToEpisodeDetailsScreen -> navigateToEpisodeDetailsScreen(action.id)
        }
    }

    private fun searchItems(searchQuery: String) {
        viewModelScope.launch {
            if (searchQuery.length > MIN_QUERY_LENGTH) {
                _state.value = SearchState.Loading
            }

            searchItemsUseCase(searchQuery).collect { searchResults ->
                foundItems = searchResults
                _state.value = SearchState.DataLoaded(foundItems)
            }
        }
    }

    private fun navigateToCharacterDetailsScreen(id: String) {
        viewModelScope.launch {
            _event.tryEmit(SearchEvent.NavigateToCharacterDetailsScreen(id))
        }
    }

    private fun navigateToEpisodeDetailsScreen(id: String) {
        viewModelScope.launch {
            _event.tryEmit(SearchEvent.NavigateToEpisodeDetailsScreen(id))
        }
    }
}