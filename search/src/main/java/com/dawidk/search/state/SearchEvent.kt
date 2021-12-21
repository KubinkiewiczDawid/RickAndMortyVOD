package com.dawidk.search.state

sealed class SearchEvent {
    data class NavigateToCharacterDetailsScreen(val id: String) : SearchEvent()
    data class NavigateToEpisodeDetailsScreen(val id: String) : SearchEvent()
}