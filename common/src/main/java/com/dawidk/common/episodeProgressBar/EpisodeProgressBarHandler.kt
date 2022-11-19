package com.dawidk.common.episodeProgressBar

import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.dawidk.core.datastore.VideoStateDataStoreRepository
import com.dawidk.common.utils.setColorFilter

class EpisodeProgressBarHandler(
    private val videoStateDataStoreRepository: VideoStateDataStoreRepository,
) {

    private var videoProgress = 0L
    private var videoDuration = 0L

    suspend fun setProgressBar(progressBar: ProgressBar, episodeId: String) {
        setColorFilter(progressBar)

        videoStateDataStoreRepository.videoStateFlow.collect {
            videoProgress = it.videoMapMap[episodeId]?.videoProgress ?: 0L
            videoDuration = it.videoMapMap[episodeId]?.videoDuration ?: 0L

            if (videoProgress > 0) {
                progressBar.isVisible = true
                progressBar.progress = (100.0 * videoProgress / videoDuration).toInt()
            }
        }
    }
}