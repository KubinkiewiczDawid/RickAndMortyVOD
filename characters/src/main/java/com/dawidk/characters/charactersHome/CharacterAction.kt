package com.dawidk.characters.charactersHome

sealed class CharacterAction {
    object Init : CharacterAction()
    object Load : CharacterAction()
    object DataLoaded : CharacterAction()
    data class HandleError(val error: Throwable) : CharacterAction()
    data class NavigateToDetailsScreen(val id: String) : CharacterAction()
}