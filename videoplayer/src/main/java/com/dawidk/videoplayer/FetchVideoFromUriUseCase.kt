package com.dawidk.videoplayer

import com.dawidk.common.video.MediaItem
import com.dawidk.videoplayer.cast.VideoProvider

private const val CATALOG_URL =
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/f.json"

class FetchVideoFromUriUseCase {

    suspend operator fun invoke(uri: String): MediaItem {
        VideoProvider.buildMedia(CATALOG_URL).let {
            it.filter { mediaItem ->
                mediaItem.url == uri
            }.map { mappedMediaItem ->
                return mappedMediaItem
            }
            return it.first()
        }
    }
}