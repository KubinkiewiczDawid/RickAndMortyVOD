package com.dawidk.videoplayer.mvi

import com.dawidk.common.mvi.ViewState
import com.google.android.exoplayer2.Player
import com.google.android.gms.cast.MediaInfo
import com.dawidk.videoplayer.model.Video

sealed class VideoPlayerState : ViewState {
    object Loading : VideoPlayerState()
    data class PlayerSet(val player: Player?) : VideoPlayerState()
    data class Error(val exception: Throwable) : VideoPlayerState()
    data class FillVideoDataSuccess(val video: Video) : VideoPlayerState()
    object OpenExpandedCastControls : VideoPlayerState()
}