package com.dawidk.common.video

import android.app.Application
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.flow.Flow

interface VideoPlayerApi<T> {

    var player: T?
    fun initializePlayer(
        playerView: PlayerView?,
        application: Application,
        videoUri: String,
        adTagUri: String,
        playWhenReady: Boolean,
        contentPosition: Long = 0
    ): Flow<T?>

    fun releasePlayer(): T?
    fun getPlayerVideoProgress(): Long
    fun restorePlayerState(playerPosition: Long, playerPlayWhenReady: Boolean)
    fun getVideoDuration(): Long
}