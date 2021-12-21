package com.dawidk.videoplayer.di

import com.dawidk.common.navigation.VideoPlayerActivityNavigator
import com.dawidk.videoplayer.FetchRandomAdTagUseCase
import com.dawidk.videoplayer.FetchRandomVideoUseCase
import com.dawidk.videoplayer.FetchVideoFromUriUseCase
import com.dawidk.videoplayer.VideoPlayerViewModel
import com.dawidk.videoplayer.cast.service.CastVideoService
import com.dawidk.videoplayer.navigation.VideoPlayerActivityNavigatorHandler
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val videoModule = module {
    viewModel { VideoPlayerViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    single { FetchRandomVideoUseCase() }
    single { FetchVideoFromUriUseCase() }
    single { CastVideoService() }
    single { FetchRandomAdTagUseCase(get()) }
    single<VideoPlayerActivityNavigator> { VideoPlayerActivityNavigatorHandler(get()) }
}