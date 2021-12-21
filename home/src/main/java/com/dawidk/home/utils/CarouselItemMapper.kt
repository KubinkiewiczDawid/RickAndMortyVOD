package com.dawidk.home.utils

import com.dawidk.core.domain.model.Character
import com.dawidk.core.domain.model.Episode
import com.dawidk.core.domain.model.Location
import com.dawidk.home.model.CarouselItem

fun Character.mapToCarouselItem() = CarouselItem.CharacterItem(
    id = id,
    name = name,
    image = image
)

fun Location.mapToCarouselItem() = CarouselItem.LocationItem(
    id = id,
    name = name,
    dimension = dimension
)

fun Episode.mapToCarouselItem() = CarouselItem.EpisodeItem(
    id = id,
    name = name,
    episode = episode,
    characters = characters
)