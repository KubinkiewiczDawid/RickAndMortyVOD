package com.dawidk.home.model

import android.os.Parcelable
import com.dawidk.core.domain.model.Character
import kotlinx.parcelize.Parcelize

sealed class PlaylistItem : Parcelable {
    @Parcelize
    data class CharacterItem(val id: String, val name: String, val image: String) : PlaylistItem()

    @Parcelize
    data class EpisodeItem(
        val id: String,
        val name: String,
        val episode: String,
        val characters: List<Character>
    ) : PlaylistItem()

    @Parcelize
    data class LocationItem(val id: String, val name: String) : PlaylistItem()

    @Parcelize
    data class SeeAllItem(val itemsList: List<PlaylistItem>) : PlaylistItem()
}