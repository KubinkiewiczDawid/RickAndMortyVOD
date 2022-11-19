package com.dawidk.core.firestore

import kotlinx.coroutines.flow.Flow

interface FirestoreClientApi {

    fun updateLastSeenPlaylist(episodeId: String)
    suspend fun getLastSeenPlaylist(): Flow<List<String>>
}