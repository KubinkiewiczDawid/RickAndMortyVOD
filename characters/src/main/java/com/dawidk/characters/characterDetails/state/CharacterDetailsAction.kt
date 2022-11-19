package com.dawidk.characters.characterDetails.state

sealed class CharacterDetailsAction {
    data class GetCharacterById(val id: String) : CharacterDetailsAction()
    data class NavigateToEpisodeDetailsScreen(val episodeId: String) : CharacterDetailsAction()
    object GetEpisodes : CharacterDetailsAction()
    object GetLocations : CharacterDetailsAction()
}