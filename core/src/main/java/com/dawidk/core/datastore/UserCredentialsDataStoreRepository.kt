package com.dawidk.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

class UserCredentialsDataStoreRepository(context: Context) {

    private val Context.userCredentialsDataStore: DataStore<UserCredentials> by dataStore(
        fileName = "user_credentials.pb",
        serializer = UserCredentialsSerializer
    )
    private val dataStore = context.userCredentialsDataStore
    val userCredentialsFlow: Flow<UserCredentials> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(UserCredentials.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun clearUserCredentials() {
        dataStore.updateData { store ->
            store.toBuilder()
                .clearUserId()
                .clearUserDisplayName()
                .clearUserEmail()
                .clear()
                .build()
        }
    }

    suspend fun updateUserId(userId: String) {
        dataStore.updateData {
            it.toBuilder().setUserId(userId).build()
        }
    }

    suspend fun updateUserDisplayName(userDisplayName: String) {
        dataStore.updateData {
            it.toBuilder().setUserDisplayName(userDisplayName).build()
        }
    }

    suspend fun updateUserEmail(userEmail: String) {
        dataStore.updateData {
            it.toBuilder().setUserEmail(userEmail).build()
        }
    }
}