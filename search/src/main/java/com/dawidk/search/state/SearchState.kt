package com.dawidk.search.state

import com.dawidk.search.model.SearchItem

sealed class SearchState {
    object Loading : SearchState()
    data class DataLoaded(val data: List<SearchItem>) : SearchState()
    data class Error(val exception: Throwable) : SearchState()
}