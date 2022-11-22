package com.dawidk.episodes.di

import com.dawidk.episodes.EpisodesViewModel
import com.dawidk.episodes.episodeDetails.EpisodeDetailsItemsProvider
import com.dawidk.episodes.episodeDetails.EpisodeDetailsViewModel
import com.dawidk.episodes.episodeDetails.navigation.EpisodeDetailsNavigator
import com.dawidk.episodes.navigation.EpisodesNavigator
import com.dawidk.episodes.usecase.FetchEpisodesListUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val episodesModule = module {
    viewModel { EpisodesViewModel(get()) }
    viewModel { EpisodeDetailsViewModel(get(), get(), get()) }
    single { FetchEpisodesListUseCase(get(), get(), get()) }
    single { EpisodesNavigator(get()) }
    single { EpisodeDetailsNavigator(get()) }
    single { EpisodeDetailsItemsProvider() }
}