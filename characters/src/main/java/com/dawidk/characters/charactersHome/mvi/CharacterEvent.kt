package com.dawidk.characters.charactersHome.mvi

import com.dawidk.common.mvi.ViewEvent

sealed class CharacterEvent: ViewEvent {
    data class NavigateToCharacterDetails(val id: String) : CharacterEvent()
}