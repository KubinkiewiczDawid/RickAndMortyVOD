package com.dawidk.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

class HomeScreenDataStoreRepository(context: Context) {

    private val Context.homeScreenDataStore: DataStore<HomeScreenContents> by dataStore(
        fileName = "home_screen_contents.pb",
        serializer = HomeScreenSerializer
    )
    private val dataStore = context.homeScreenDataStore
    val homeScreenFlow: Flow<HomeScreenContents> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(HomeScreenContents.getDefaultInstance())
            } else {
                throw exception
            }
        }

    private suspend fun clearAllPlaylists() {
        dataStore.updateData { store ->
            store.toBuilder()
                .clearPlaylists()
                .clear()
                .build()
        }
    }

    private suspend fun updatePlaylist(playlistItems: ItemsList.Builder) {
        dataStore.updateData { store ->
            store.toBuilder()
                .addPlaylists(playlistItems)
                .build()
        }
    }

    suspend fun updatePlaylists(items: List<Pair<String, HomeScreenItem.ItemType>>) {
        clearAllPlaylists()
        val playlistItems = ItemsList.newBuilder()

        items.map {
            HomeScreenItem.newBuilder().setItemId(it.first).setType(it.second).build()
        }.forEach {
            playlistItems.addItems(it)
        }

        updatePlaylist(playlistItems)
    }

    suspend fun updateCarousel(items: List<Pair<String, HomeScreenItem.ItemType>>) {
        val carouselItems = ItemsList.newBuilder()

        items.map {
            HomeScreenItem.newBuilder().setItemId(it.first).setType(it.second).build()
        }.forEach {
            carouselItems.addItems(it)
        }

        dataStore.updateData { store ->
            store.toBuilder()
                .clearCarousel()
                .addCarousel(carouselItems)
                .build()
        }
    }

    suspend fun setTimestamp(timestamp: Long) {
        dataStore.updateData { store ->
            store.toBuilder()
                .clearTimestamp()
                .setTimestamp(timestamp)
                .build()
        }
    }

    suspend fun updateEpisodesList(id: String) {
        dataStore.updateData { store ->
            store.toBuilder()
                .addLastSeenEpisodeId(id)
                .build()
        }
    }

    suspend fun updateLastSeenCharacter(id: String) {
        dataStore.updateData { lastWatched ->
            lastWatched.toBuilder().setLastSeenCharacterId(id).build()
        }
    }

    suspend fun clearAll() {
        dataStore.updateData { store ->
            store.toBuilder()
                .clearPlaylists()
                .clearCarousel()
                .clearTimestamp()
                .clearLastSeenCharacterId()
                .clearLastSeenEpisodeId()
                .clear()
                .build()
        }
    }
}