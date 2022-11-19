package com.dawidk.episodes.model

import com.dawidk.core.domain.model.Character

sealed class EpisodeItem {
    data class EpisodeInfoItem(
        val name: String,
        val air_date: String,
        val characters: List<Character>,
        val season: String
    ) : EpisodeItem()

    object EpisodeTabItem : EpisodeItem()
    data class EpisodeCharacterItem(val character: Character) : EpisodeItem()
}