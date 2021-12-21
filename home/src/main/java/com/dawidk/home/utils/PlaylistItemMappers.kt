package com.dawidk.home.utils

import com.dawidk.core.domain.model.Character
import com.dawidk.core.domain.model.Episode
import com.dawidk.core.domain.model.Location
import com.dawidk.home.model.PlaylistItem

fun Character.mapToPlaylistItem(): PlaylistItem.CharacterItem =
    PlaylistItem.CharacterItem(id = id, name = name, image = image)

fun Episode.mapToPlaylistItem() =
    PlaylistItem.EpisodeItem(id = id, name = name, episode = episode, characters = characters)

fun Location.mapToPlaylistItem() = PlaylistItem.LocationItem(id = id, name = name)