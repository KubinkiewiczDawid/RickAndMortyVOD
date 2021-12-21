package com.dawidk.characters.model

import com.dawidk.core.domain.model.CharacterStatus
import com.dawidk.core.domain.model.Episode
import com.dawidk.core.domain.model.Location

sealed class CharacterItem {
    data class CharacterInfoItem(
        val name: String,
        val status: CharacterStatus,
        val species: String,
        val gender: String,
        val image: String,
        val episode: Episode,
        val created: String
    ) : CharacterItem()

    data class CharacterEpisodesItem(val episode: Episode) : CharacterItem()
    data class CharacterLocationItem(val location: Location) : CharacterItem()
    object CharacterTabItem : CharacterItem()
}
