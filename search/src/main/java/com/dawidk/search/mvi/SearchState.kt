package com.dawidk.search.mvi

import com.dawidk.common.mvi.ViewState
import com.dawidk.search.model.SearchItem

sealed class SearchState: ViewState {
    object Loading : SearchState()
    data class DataLoaded(val data: List<SearchItem>) : SearchState()
    data class Error(val exception: Throwable) : SearchState()
}