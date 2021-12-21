package com.dawidk.search.model

import com.dawidk.core.domain.model.Character

sealed class SearchItem {
    data class CharacterItem(val id: String, val name: String, val image: String) : SearchItem()
    data class EpisodeItem(
        val id: String,
        val name: String,
        val episode: String,
        val characters: List<Character>
    ) : SearchItem()

    data class LocationItem(val id: String, val name: String) : SearchItem()
}