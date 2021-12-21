package com.dawidk.videoplayer

import com.dawidk.common.video.MediaItem
import com.dawidk.videoplayer.cast.VideoProvider
import kotlin.random.Random

private const val CATALOG_URL =
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/f.json"

class FetchRandomVideoUseCase {

    suspend operator fun invoke(): MediaItem {
        VideoProvider.buildMedia(CATALOG_URL).let {
            val randomIndex = Random.nextInt(it.size)
            return it[randomIndex]
        }
    }
}