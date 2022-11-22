package com.dawidk.search.mvi

import com.dawidk.common.mvi.ViewAction

sealed class SearchAction: ViewAction {
    data class Search(val name: String) : SearchAction()
    object Load : SearchAction()
    object DataLoaded : SearchAction()
    data class HandleError(val error: Throwable) : SearchAction()
    data class NavigateToCharacterDetailsScreen(val id: String) : SearchAction()
    data class NavigateToEpisodeDetailsScreen(val id: String) : SearchAction()
}