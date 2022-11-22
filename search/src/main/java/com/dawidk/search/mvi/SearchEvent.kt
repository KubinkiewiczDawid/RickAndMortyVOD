package com.dawidk.search.mvi

import com.dawidk.common.mvi.ViewEvent

sealed class SearchEvent: ViewEvent {
    data class NavigateToCharacterDetailsScreen(val id: String) : SearchEvent()
    data class NavigateToEpisodeDetailsScreen(val id: String) : SearchEvent()
}