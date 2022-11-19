package com.dawidk.core.firestore

import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.dawidk.core.datastore.UserCredentialsDataStoreRepository
import com.dawidk.core.domain.mappers.mapToAccountInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

const val COLLECTION_ID = "usersPlaylists"
const val LAST_SEEN_PLAYLIST_SIZE = 4
const val EMAIL_KEY = "userEmail"
const val PLAYLIST_KEY = "playlist"

class FirestoreClient(
    private val userCredentialsDataStoreRepository: UserCredentialsDataStoreRepository,
    scope: CoroutineScope
) :
    FirestoreClientApi {

    private val firestoreDatabase = Firebase.firestore

    var userEmail = ""

    init {
        scope.launch {
            userCredentialsDataStoreRepository.userCredentialsFlow.collect {
                userEmail = it.mapToAccountInfo().email
            }
        }
    }

    override fun updateLastSeenPlaylist(episodeId: String) {
        firestoreDatabase.collection(COLLECTION_ID).whereEqualTo(EMAIL_KEY, userEmail).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result.isEmpty) {
                    val playlist = hashMapOf(
                        EMAIL_KEY to userEmail,
                        PLAYLIST_KEY to listOf(episodeId)
                    )
                    firestoreDatabase.collection(COLLECTION_ID).add(playlist)
                } else if (task.isSuccessful && !task.result.isEmpty) {
                    val currentPlaylist =
                        task.result.documents.first().data?.get(PLAYLIST_KEY) as MutableList<String>
                    if (episodeId != currentPlaylist.first()) {
                        currentPlaylist.add(0, episodeId)
                    }
                    firestoreDatabase.collection(COLLECTION_ID).document(task.result.documents.first().id)
                        .update(PLAYLIST_KEY, currentPlaylist.take(LAST_SEEN_PLAYLIST_SIZE))
                }
            }
    }

    override suspend fun getLastSeenPlaylist(): Flow<List<String>> = callbackFlow {
        var subscription: ListenerRegistration? = null
        firestoreDatabase.collection(COLLECTION_ID).whereEqualTo(EMAIL_KEY, userEmail).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && !task.result.isEmpty) {
                    val userPlaylistDocument =
                        firestoreDatabase.collection(COLLECTION_ID).document(task.result.documents.first().id)
                    subscription = userPlaylistDocument.addSnapshotListener { snapshot, _ ->
                        if (snapshot!!.exists()) {
                            val playlist = snapshot.data?.get(PLAYLIST_KEY) as MutableList<String>
                            this.trySend(playlist).isSuccess
                        }
                    }
                } else {
                    this.trySend(emptyList<String>()).isSuccess
                }
            }
        awaitClose {
            subscription?.remove()
        }
    }
}