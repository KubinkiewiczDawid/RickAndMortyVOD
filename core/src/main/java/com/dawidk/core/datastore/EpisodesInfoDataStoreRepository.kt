package com.dawidk.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

class EpisodesInfoDataStoreRepository(context: Context) {

    private val Context.episodesInfoDataStore: DataStore<EpisodesInfo> by dataStore(
        fileName = "episodes_data.pb",
        serializer = EpisodesInfoSerializer
    )
    private val dataStore = context.episodesInfoDataStore
    val episodesInfoFlow: Flow<EpisodesInfo> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(EpisodesInfo.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun clearEpisodesInfo() {
        dataStore.updateData { store ->
            store.toBuilder()
                .clearEpisodesCount()
                .clear()
                .build()
        }
    }

    suspend fun updateEpisodesCount(episodesCount: Int) {
        dataStore.updateData {
            it.toBuilder().setEpisodesCount(episodesCount).build()
        }
    }
}