package com.dawidk.common.di

import com.dawidk.common.ApplicationProvider
import com.google.android.exoplayer2.Player
import com.dawidk.common.episodeProgressBar.EpisodeProgressBarHandler
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.common.video.ExoPlayerVideoApiWrapper
import com.dawidk.common.video.VideoPlayerApi
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedModule = module {
    single { NetworkMonitor(androidContext()) }
    single<VideoPlayerApi<Player>> { ExoPlayerVideoApiWrapper() }
    single { EpisodeProgressBarHandler(get()) }
    single { ApplicationProvider(get()) }
}