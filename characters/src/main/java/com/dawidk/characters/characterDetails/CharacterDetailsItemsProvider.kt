package com.dawidk.characters.characterDetails

import com.dawidk.characters.model.CharacterItem
import com.dawidk.core.domain.model.Character

class CharacterDetailsItemsProvider {

    operator fun invoke(character: Character): List<CharacterItem> {
        return insertHeaderData(character) +
                character.episode.map {
                    CharacterItem.CharacterEpisodesItem(
                        episode = it
                    )
                } +
                listOf(
                    CharacterItem.CharacterLocationItem(character.origin),
                    CharacterItem.CharacterLocationItem(character.location)
                )
    }

    private fun insertHeaderData(
        character: Character
    ): List<CharacterItem> {
        return listOf(
            with(character) {
                CharacterItem.CharacterInfoItem(
                    name = name,
                    status = status,
                    species = species,
                    gender = gender,
                    image = image,
                    episode = episode.first(),
                    created = created
                )
            },
            CharacterItem.CharacterTabItem
        )
    }
}