package com.dawidk.characters.characterDetails

import com.dawidk.characters.model.CharacterItem

sealed class CharacterDetailsState {
    data class GetCharacterSuccess(val characterItems: List<CharacterItem>) :
        CharacterDetailsState()

    object GetEpisodes : CharacterDetailsState()
    object GetLocations : CharacterDetailsState()
    data class Error(val exception: Throwable, val characterId: String) : CharacterDetailsState()
    object Loading : CharacterDetailsState()
}