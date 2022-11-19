package com.dawidk.videoplayer.navigation

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.dawidk.common.navigation.VideoPlayerActivityNavigator
import com.dawidk.common.video.VideoType
import com.dawidk.videoplayer.VideoPlayerActivity

private const val EPISODE_ID_KEY = "id"
private const val EPISODE_VIDEO_TYPE_KEY = "videoType"

class VideoPlayerActivityNavigatorHandler(
    private val context: Context
) : VideoPlayerActivityNavigator {

    override fun startVideoPlayerActivity(id: String, videoType: VideoType) {
        val intent = Intent(context, VideoPlayerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(EPISODE_ID_KEY, id)
        intent.putExtra(EPISODE_VIDEO_TYPE_KEY, videoType)
        ContextCompat.startActivity(context, intent, null)
    }
}