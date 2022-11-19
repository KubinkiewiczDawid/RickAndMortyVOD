package com.dawidk.common.video

import android.app.Application
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader.Builder
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource.Factory
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExoPlayerVideoApiWrapper : VideoPlayerApi<Player> {

    override var player: Player? = null
    private var adsPlayerView: PlayerView? = null
    private var adsLoader: ImaAdsLoader? = null

    private fun initializeAdsLoader(
        playerView: PlayerView?,
        application: Application
    ): DefaultMediaSourceFactory {
        adsLoader = Builder(application).build()
        adsPlayerView = playerView

        val dataSourceFactory: Factory =
            DefaultDataSourceFactory(application, Util.getUserAgent(application, ""))
        return DefaultMediaSourceFactory(dataSourceFactory)
            .setAdsLoaderProvider { adsLoader }
            .setAdViewProvider(adsPlayerView)
    }

    override fun initializePlayer(
        playerView: PlayerView?,
        application: Application,
        videoUri: String,
        adTagUri: String,
        playWhenReady: Boolean,
        contentPosition: Long
    ): Flow<Player?> = flow {

        val mediaSourceFactory = initializeAdsLoader(playerView, application)
        player = SimpleExoPlayer.Builder(application)
            .setMediaSourceFactory(mediaSourceFactory).build()
        adsPlayerView?.player = player
        adsLoader?.setPlayer(player)

        val mediaItem = MediaItem.Builder()
            .setUri(videoUri).setAdTagUri(adTagUri).build()

        player?.apply {
            setMediaItem(mediaItem)
            prepare()
            seekTo(contentPosition)
            this.playWhenReady = playWhenReady
        }

        emit(player)
    }

    override fun releasePlayer(): Player? {
        player?.release()
        player = null
        return player
    }

    override fun getPlayerVideoProgress(): Long {
        return player?.contentPosition ?: 0
    }

    override fun restorePlayerState(playerPosition: Long, playerPlayWhenReady: Boolean) {
        player?.seekTo(playerPosition)
        player?.playWhenReady = playerPlayWhenReady
    }

    override fun getVideoDuration(): Long {
        return player?.contentDuration ?: 0
    }
}