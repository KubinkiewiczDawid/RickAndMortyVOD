package com.dawidk.videoplayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.dawidk.common.video.MediaItem
import com.dawidk.common.video.VideoPlayerApi
import com.dawidk.common.video.VideoType
import com.dawidk.core.datastore.VideoStateDataStoreRepository
import com.dawidk.core.domain.model.Episode
import com.dawidk.core.executors.FetchEpisodeByIdExecutor
import com.dawidk.core.utils.DataLoadingException
import com.dawidk.videoplayer.cast.service.CastVideoService
import com.dawidk.videoplayer.cast.service.state.CastVideoEvent
import com.dawidk.videoplayer.cast.service.state.CastVideoState
import com.dawidk.videoplayer.model.Video
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class VideoPlayerViewModel(
    application: Application,
    private val fetchRandomVideoUseCase: FetchRandomVideoUseCase,
    private val fetchVideoFromUriUseCase: FetchVideoFromUriUseCase,
    private val fetchEpisodeByIdExecutor: FetchEpisodeByIdExecutor,
    private val videoPlayerApi: VideoPlayerApi<Player>,
    private val videoStateDataStoreRepository: VideoStateDataStoreRepository,
    private val castVideoService: CastVideoService,
    private val fetchRandomAdTagUseCase: FetchRandomAdTagUseCase
) : AndroidViewModel(application), LifecycleObserver {

    private val _state: MutableStateFlow<VideoPlayerState> =
        MutableStateFlow(VideoPlayerState.Loading)
    val state: StateFlow<VideoPlayerState> = _state
    private var mediaItem: MediaItem = MediaItem.EMPTY
    private var videoId: String = ""
    private var initializeJob: Job? = null
    private var releaseJob: Job? = null

    init {
        castVideoService.init(getApplication())
        registerCastVideoServiceStateListener()
        registerCastVideoServiceEventListener()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        castVideoService.releaseIfNotActive()
        releasePlayer()
    }

    fun onAction(action: VideoPlayerAction) {
        when (action) {
            is VideoPlayerAction.FillVideoData ->
                viewModelScope.launch { fillVideoData(action.id, action.videoType) }
            is VideoPlayerAction.PauseVideo -> pauseVideoPlayer()
            is VideoPlayerAction.ReinitializeVideo -> initializeVideo(action.playerView)
            is VideoPlayerAction.StartVideoPlayer -> {
                initializeVideo(action.playerView)
            }
        }
    }

    private fun pauseVideoPlayer() {
        videoPlayerApi.player?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    private fun initializeVideo(playerView: PlayerView) {
        assignVideoUrl()
        if (castVideoService.isSessionActive()) {
            createCastMedia()
        } else {
            initializePlayer(playerView)
        }
    }

    private suspend fun fillVideoData(videoId: String, videoType: VideoType) {
        this.videoId = videoId
        when (videoType) {
            VideoType.EPISODE -> fetchEpisodeById(videoId)
        }
    }

    private suspend fun fetchEpisodeById(id: String) {
        _state.value = VideoPlayerState.Loading
        val episode = try {
            fetchEpisodeByIdExecutor.getEpisodeById(id)
        } catch (ex: DataLoadingException) {
            Episode.EMPTY
        }
        _state.value = VideoPlayerState.FillVideoDataSuccess(
            Video(
                name = episode.name,
                details = episode.episode,
                moreDetails = episode.airDate
            )
        )
    }

    private fun initializePlayer(playerView: PlayerView? = null) {
        initializeJob = viewModelScope.launch(CoroutineExceptionHandler { _, exception ->
            when (exception) {
                is DataLoadingException -> {
                    _state.value =
                        VideoPlayerState.Error(DataLoadingException("Cannot fetch episode details"))
                    pauseVideoPlayer()
                }
            }
        }) {
            releaseJob?.let {
                if (it.isActive) it.join()
            }
            videoStateDataStoreRepository.videoStateFlow.first().videoMapMap[videoId].let {
                val videoProgress = (it?.videoProgress ?: 0L)
                videoPlayerApi.initializePlayer(
                    playerView,
                    getApplication(),
                    mediaItem.url ?: "",
                    fetchRandomAdTagUseCase(),
                    true,
                    videoProgress
                ).collect { player ->
                    _state.value = VideoPlayerState.PlayerSet(player)
                }
            }
        }
    }

    private fun registerCastVideoServiceStateListener() {
        viewModelScope.launch {
            castVideoService.state.collect {
                when (it) {
                    is CastVideoState.ApplicationConnected -> {
                        createCastMedia()
                    }
                    is CastVideoState.ApplicationDisconnected -> {
                        initializePlayer()
                    }
                }
            }
        }
    }

    private fun registerCastVideoServiceEventListener() {
        viewModelScope.launch {
            castVideoService.event.collect {
                when (it) {
                    is CastVideoEvent.OpenExpandedControls -> {
                        _state.value = VideoPlayerState.OpenExpandedCastControls
                    }
                }
            }
        }
    }

    private fun createCastMedia() {
        viewModelScope.launch {
            if (videoPlayerApi.player != null) {
                loadCastMedia(videoPlayerApi.getPlayerVideoProgress())
            } else {
                videoStateDataStoreRepository.videoStateFlow.first().videoMapMap[videoId].let {
                    val videoProgress = (it?.videoProgress ?: 0L)
                    loadCastMedia(videoProgress)
                }
            }
        }
    }

    private fun loadCastMedia(videoProgress: Long) {
        castVideoService.loadRemoteMedia(
            videoId,
            mediaItem,
            videoProgress
        )
    }

    private fun assignVideoUrl() {
        runBlocking {
            videoStateDataStoreRepository.videoStateFlow.first().videoMapMap[videoId]?.videoUrl.let { savedUrl ->
                mediaItem = if (savedUrl.isNullOrBlank()) {
                    fetchRandomVideoUseCase()
                } else {
                    fetchVideoFromUriUseCase(savedUrl)
                }
            }
        }
    }

    private fun releasePlayer() {
        releaseJob = viewModelScope.launch {
            initializeJob?.let {
                if (it.isActive) it.cancel()
            }
            if (videoPlayerApi.player != null) {
                saveVideoState(videoPlayerApi.getPlayerVideoProgress())
                val player = videoPlayerApi.releasePlayer()
                _state.value = VideoPlayerState.PlayerSet(player)
            }
        }
    }

    private fun saveVideoState(videoProgress: Long) {
        runBlocking {
            videoStateDataStoreRepository.updateVideoMap(
                videoId,
                mediaItem.url ?: "",
                videoProgress,
                videoPlayerApi.getVideoDuration()
            )
        }
    }
}