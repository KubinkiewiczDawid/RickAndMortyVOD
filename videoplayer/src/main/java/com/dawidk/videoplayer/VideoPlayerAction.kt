package com.dawidk.videoplayer

import com.google.android.exoplayer2.ui.PlayerView
import com.dawidk.common.video.VideoType

sealed class VideoPlayerAction {
    data class FillVideoData(val id: String, val videoType: VideoType) : VideoPlayerAction()
    object PauseVideo : VideoPlayerAction()
    data class ReinitializeVideo(val playerView: PlayerView) : VideoPlayerAction()
    data class StartVideoPlayer(val playerView: PlayerView) : VideoPlayerAction()
}