package com.dawidk.common.navigation

import com.dawidk.common.video.VideoType

interface VideoPlayerActivityNavigator {

    fun startVideoPlayerActivity(id: String, videoType: VideoType)
}