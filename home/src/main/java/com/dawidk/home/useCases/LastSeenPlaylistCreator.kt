package com.dawidk.home.useCases

import com.dawidk.core.domain.model.Episode
import com.dawidk.core.executors.FetchEpisodeByIdExecutor
import com.dawidk.core.utils.DataLoadingException
import com.dawidk.home.model.Playlist
import com.dawidk.home.utils.mapToPlaylistItem

class LastSeenPlaylistCreator(private val fetchEpisodeByIdExecutor: FetchEpisodeByIdExecutor) {

    suspend fun createPlaylist(lastSeenItems: List<String>): Playlist? {
        val lastSeenEpisodes = lastSeenItems.map { episodeId ->
            fetchEpisodeById(episodeId).mapToPlaylistItem()
        }

        return if (lastSeenEpisodes.size > 1) Playlist(
            name = "Last Seen",
            items = lastSeenEpisodes
        ) else null
    }

    private suspend fun fetchEpisodeById(id: String): Episode {
        return try {
            fetchEpisodeByIdExecutor.getEpisodeById(id)
        } catch (ex: DataLoadingException) {
            Episode.EMPTY
        }
    }
}