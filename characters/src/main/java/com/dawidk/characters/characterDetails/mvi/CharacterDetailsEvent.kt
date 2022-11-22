package com.dawidk.characters.characterDetails.mvi

import com.dawidk.common.mvi.ViewEvent

sealed class CharacterDetailsEvent: ViewEvent {
    data class NavigateToEpisodeDetails(val episodeId: String) : CharacterDetailsEvent()
}
