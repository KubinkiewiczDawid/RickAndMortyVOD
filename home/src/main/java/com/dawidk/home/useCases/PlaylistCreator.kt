package com.dawidk.home.useCases

import com.dawidk.core.datastore.HomeScreenContents
import com.dawidk.core.datastore.HomeScreenItem
import com.dawidk.core.domain.model.Character
import com.dawidk.core.domain.model.Episode
import com.dawidk.core.domain.model.Location
import com.dawidk.core.executors.FetchCharacterByIdExecutor
import com.dawidk.core.executors.FetchEpisodeByIdExecutor
import com.dawidk.core.executors.FetchLocationByIdExecutor
import com.dawidk.core.utils.DataLoadingException
import com.dawidk.home.model.Playlist
import com.dawidk.home.model.PlaylistItem
import com.dawidk.home.model.PlaylistItem.SeeAllItem
import com.dawidk.home.utils.mapToPlaylistItem

private const val MAX_PLAYLIST_ITEMS_COUNT = 8

class PlaylistCreator(
    private val fetchCharacterByIdExecutor: FetchCharacterByIdExecutor,
    private val fetchLocationByIdExecutor: FetchLocationByIdExecutor,
    private val fetchEpisodeByIdExecutor: FetchEpisodeByIdExecutor,
) {

    suspend fun createPlaylists(list: HomeScreenContents): List<Playlist> {
        val playlistsList = emptyList<Playlist>().toMutableList()

        list.playlistsList.forEachIndexed { i, playlist ->
            val items: MutableList<PlaylistItem> = emptyList<PlaylistItem>().toMutableList()
            playlist.itemsList.forEach { item ->
                when (item.type) {
                    HomeScreenItem.ItemType.CHARACTER -> items += fetchCharacterById(item.itemId).mapToPlaylistItem()
                    HomeScreenItem.ItemType.EPISODE -> items += fetchEpisodeById(item.itemId).mapToPlaylistItem()
                    else -> items += fetchLocationById(item.itemId).mapToPlaylistItem()
                }
            }
            playlistsList += Playlist(
                name = "Playlist ${i + 1}", items = items.take(
                    MAX_PLAYLIST_ITEMS_COUNT
                ) + SeeAllItem(items)
            )
        }

        return playlistsList
    }

    private suspend fun fetchCharacterById(id: String): Character {
        return try {
            fetchCharacterByIdExecutor.getCharacterById(id)
        } catch (ex: DataLoadingException) {
            Character.EMPTY
        }
    }

    private suspend fun fetchLocationById(id: String): Location {
        return try {
            fetchLocationByIdExecutor.getLocationById(id)
        } catch (ex: DataLoadingException) {
            Location.EMPTY
        }
    }

    private suspend fun fetchEpisodeById(id: String): Episode {
        return try {
            fetchEpisodeByIdExecutor.getEpisodeById(id)
        } catch (ex: DataLoadingException) {
            Episode.EMPTY
        }
    }
}