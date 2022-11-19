package com.dawidk.characters.characterDetails.state

sealed class CharacterDetailsEvent {
    data class NavigateToEpisodeDetails(val episodeId: String) : CharacterDetailsEvent()
}
