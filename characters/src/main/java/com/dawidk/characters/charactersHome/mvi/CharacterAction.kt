package com.dawidk.characters.charactersHome.mvi

import com.dawidk.common.mvi.ViewAction

sealed class CharacterAction: ViewAction {
    object Init : CharacterAction()
    object Load : CharacterAction()
    object DataLoaded : CharacterAction()
    data class HandleError(val error: Throwable) : CharacterAction()
    data class NavigateToDetailsScreen(val id: String) : CharacterAction()
}