package com.dawidk.home.model

import com.dawidk.core.domain.model.Character

sealed class CarouselItem {
    data class CharacterItem(val id: String, val name: String, val image: String) : CarouselItem()
    data class LocationItem(val id: String, val name: String, val dimension: String) :
        CarouselItem()

    data class EpisodeItem(
        val id: String,
        val name: String,
        val episode: String,
        val characters: List<Character>
    ) : CarouselItem()
}
