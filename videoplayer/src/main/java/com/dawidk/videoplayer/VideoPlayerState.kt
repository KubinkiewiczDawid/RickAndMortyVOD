package com.dawidk.videoplayer

import com.google.android.exoplayer2.Player
import com.google.android.gms.cast.MediaInfo
import com.dawidk.videoplayer.model.Video

sealed class VideoPlayerState {
    object Loading : VideoPlayerState()
    data class PlayerSet(val player: Player?) :
        VideoPlayerState()

    data class Error(val exception: Throwable) : VideoPlayerState()
    data class FillVideoDataSuccess(val video: Video) : VideoPlayerState()
    data class CastMediaCreated(val mediaInfo: MediaInfo, val videoProgress: Long) :
        VideoPlayerState()

    object OpenExpandedCastControls : VideoPlayerState()
}