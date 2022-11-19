package com.dawidk.characters.charactersHome

sealed class CharacterEvent {
    data class NavigateToCharacterDetails(val id: String) : CharacterEvent()
}