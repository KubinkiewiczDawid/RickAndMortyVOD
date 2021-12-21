package com.dawidk.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

class VideoStateDataStoreRepository(context: Context) {

    private val Context.videoStateDataStore: DataStore<VideoState> by dataStore(
        fileName = "video_state.pb",
        serializer = VideoStateSerializer
    )

    private val dataStore = context.videoStateDataStore

    val videoStateFlow: Flow<VideoState> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(VideoState.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun clearVideoState() {
        dataStore.updateData { store ->
            store.toBuilder()
                .clearVideoMap()
                .clear()
                .build()
        }
    }

    suspend fun updateVideoMap(
        videoId: String,
        videoUrl: String,
        videoProgress: Long,
        videoDuration: Long
    ): VideoState {
        val videoItem = VideoItem.newBuilder().setVideoUrl(videoUrl).setVideoProgress(videoProgress)
            .setVideoDuration(videoDuration).build()
        return dataStore.updateData { store ->
            store.toBuilder()
                .putVideoMap(videoId, videoItem)
                .build()
        }
    }
}