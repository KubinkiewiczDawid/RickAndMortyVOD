package com.dawidk.videoplayer.cast.service

import android.content.Context
import android.net.Uri
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManager
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.images.WebImage
import com.dawidk.common.video.MediaItem
import com.dawidk.videoplayer.cast.SessionManagerListenerHandler
import com.dawidk.videoplayer.cast.SessionManagerListenerImpl
import com.dawidk.videoplayer.cast.service.state.CastVideoEvent
import com.dawidk.videoplayer.cast.service.state.CastVideoState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class CastVideoService {

    private val _state: MutableStateFlow<CastVideoState> =
        MutableStateFlow(CastVideoState.Idle)
    val state: StateFlow<CastVideoState> = _state
    private val _event: MutableSharedFlow<CastVideoEvent> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<CastVideoEvent> = _event
    private var castSession: CastSession? = null
    private lateinit var sessionManager: SessionManager
    private lateinit var sessionManagerListenerImpl: SessionManagerListenerImpl
    var castedVideoId: String? = null

    fun init(context: Context) {
        sessionManager = CastContext.getSharedInstance(context).sessionManager
        castSession = sessionManager.currentCastSession
        createSessionManagerListener()
        sessionManager.addSessionManagerListener(
            sessionManagerListenerImpl,
            CastSession::class.java
        )
    }

    private fun createSessionManagerListener() {
        sessionManagerListenerImpl =
            SessionManagerListenerImpl(object : SessionManagerListenerHandler {
                override fun onApplicationConnected(session: CastSession) {
                    castSession = session
                    _state.value = CastVideoState.ApplicationConnected
                }

                override fun onApplicationDisconnected() {
                    castedVideoId = null
                    _state.value = CastVideoState.ApplicationDisconnected
                    release()
                }
            })
    }

    fun releaseIfNotActive() {
        if (!isSessionActive())
            release()
    }

    fun isSessionActive() = castSession != null && castSession!!.isConnected

    private fun release() {
        sessionManager.removeSessionManagerListener(
            sessionManagerListenerImpl,
            CastSession::class.java
        )
        castSession = null
    }

    fun loadRemoteMedia(videoId: String, mediaItem: MediaItem, videoProgress: Long) {
        castSession?.let { castSession ->
            this.castedVideoId = videoId
            val remoteMediaClient: RemoteMediaClient = castSession.remoteMediaClient ?: return
            remoteMediaClient.registerCallback(object : RemoteMediaClient.Callback() {
                override fun onStatusUpdated() {
                    _event.tryEmit(CastVideoEvent.OpenExpandedControls)
                    remoteMediaClient.unregisterCallback(this)
                }
            })
            remoteMediaClient.load(
                MediaLoadRequestData.Builder()
                    .setMediaInfo(createMediaInfo(mediaItem))
                    .setAutoplay(true)
                    .setCurrentTime(videoProgress)
                    .build()
            )
        }
    }

    private fun createMediaInfo(mediaItem: MediaItem): MediaInfo {
        with(mediaItem) {
            val movieMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE)
            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, subTitle ?: "")
            movieMetadata.putString(MediaMetadata.KEY_TITLE, title ?: "")
            images.forEach {
                movieMetadata.addImage(WebImage(Uri.parse(it)))
            }
            return MediaInfo.Builder(url ?: "")
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("videos/${this.contentType}")
                .setMetadata(movieMetadata)
                .setStreamDuration(duration * 1000)
                .build()
        }
    }
}