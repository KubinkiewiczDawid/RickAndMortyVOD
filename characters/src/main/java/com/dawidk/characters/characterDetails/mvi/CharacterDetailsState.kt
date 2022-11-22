package com.dawidk.characters.characterDetails.mvi

import com.dawidk.characters.model.CharacterItem
import com.dawidk.common.mvi.ViewState

sealed class CharacterDetailsState: ViewState {
    data class GetCharacterSuccess(val characterItems: List<CharacterItem>) :
        CharacterDetailsState()

    object GetEpisodes : CharacterDetailsState()
    object GetLocations : CharacterDetailsState()
    data class Error(val exception: Throwable, val characterId: String) : CharacterDetailsState()
    object Loading : CharacterDetailsState()
}