package com.dawidk.home.useCases

import com.dawidk.core.datastore.HomeScreenDataStoreRepository
import com.dawidk.core.datastore.HomeScreenItem
import com.dawidk.core.firestore.FirestoreClientApi
import com.dawidk.home.model.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

private const val PLAYLIST_MIN_COUNT = 1
private const val PLAYLIST_MAX_COUNT = 4
private const val CHARACTER_MIN_ID = 1
private const val CHARACTER_MAX_ID = 671
private const val PLAYLIST_ITEMS_COUNT = 20
private const val LOCATION_MIN_ID = 1
private const val LOCATION_MAX_ID = 108
private const val EPISODE_MIN_ID = 1
private const val EPISODE_MAX_ID = 41

enum class RandomItem {
    Character,
    Episode,
    Location
}

class FetchPlaylistsUseCase(
    private val homeScreenDataStoreRepository: HomeScreenDataStoreRepository,
    private val playlistCreator: PlaylistCreator,
    private val lastSeenPlaylistCreator: LastSeenPlaylistCreator,
    private val firestoreClient: FirestoreClientApi
) {

    fun generatePlaylists() {
        val playlistsCount = (PLAYLIST_MIN_COUNT..PLAYLIST_MAX_COUNT).random()

        for (i in 1..playlistsCount) {
            val items: MutableList<Pair<String, HomeScreenItem.ItemType>> =
                emptyList<Pair<String, HomeScreenItem.ItemType>>().toMutableList()
            for (j in 1..PLAYLIST_ITEMS_COUNT) {
                items += when (RandomItem.values().random()) {
                    RandomItem.Character -> getRandomCharacter()
                    RandomItem.Episode -> getRandomEpisode()
                    RandomItem.Location -> getRandomLocation()
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                homeScreenDataStoreRepository.updatePlaylists(items = items.shuffled())
            }
        }
    }

    private fun getRandomCharacter(): Pair<String, HomeScreenItem.ItemType> {
        val characterId =
            (CHARACTER_MIN_ID..CHARACTER_MAX_ID).random().toString()
        return Pair(characterId, HomeScreenItem.ItemType.CHARACTER)
    }

    private fun getRandomEpisode(): Pair<String, HomeScreenItem.ItemType> {
        val episodeId = (EPISODE_MIN_ID..EPISODE_MAX_ID).random().toString()
        return Pair(episodeId, HomeScreenItem.ItemType.EPISODE)
    }

    private fun getRandomLocation(): Pair<String, HomeScreenItem.ItemType> {
        val locationId =
            (LOCATION_MIN_ID..LOCATION_MAX_ID).random().toString()
        return Pair(locationId, HomeScreenItem.ItemType.LOCATION)
    }

    suspend operator fun invoke(): Flow<List<Playlist>> = flow {
        homeScreenDataStoreRepository.homeScreenFlow.combine(firestoreClient.getLastSeenPlaylist()) { playlists, lastSeenPlaylist ->
            val playlistsList = emptyList<Playlist>().toMutableList()
            lastSeenPlaylistCreator.createPlaylist(lastSeenPlaylist)?.let { playlist ->
                playlistsList += playlist
            }
            playlistsList += playlistCreator.createPlaylists(playlists)
            playlistsList
        }.collect {
            emit(it)
        }
    }
}

