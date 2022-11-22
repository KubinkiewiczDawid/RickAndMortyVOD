package com.dawidk.characters.characterDetails.mvi

import com.dawidk.common.mvi.ViewAction

sealed class CharacterDetailsAction: ViewAction {
    data class GetCharacterById(val id: String) : CharacterDetailsAction()
    data class NavigateToEpisodeDetailsScreen(val episodeId: String) : CharacterDetailsAction()
    object GetEpisodes : CharacterDetailsAction()
    object GetLocations : CharacterDetailsAction()
}