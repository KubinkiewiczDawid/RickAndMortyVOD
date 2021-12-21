package com.dawidk.search.state

sealed class SearchAction {
    data class Search(val name: String) : SearchAction()
    object Load : SearchAction()
    object DataLoaded : SearchAction()
    data class HandleError(val error: Throwable) : SearchAction()
    data class NavigateToCharacterDetailsScreen(val id: String) : SearchAction()
    data class NavigateToEpisodeDetailsScreen(val id: String) : SearchAction()
}