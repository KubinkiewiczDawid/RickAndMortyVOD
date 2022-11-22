package com.dawidk.home.di

import com.dawidk.home.HomeViewModel
import com.dawidk.home.navigation.HomeNavigator
import com.dawidk.home.useCases.FetchCarouselItemsUseCase
import com.dawidk.home.useCases.FetchHomeItemsUseCase
import com.dawidk.home.useCases.FetchPlaylistsUseCase
import com.dawidk.home.useCases.LastSeenPlaylistCreator
import com.dawidk.home.useCases.PlaylistCreator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel { HomeViewModel(get()) }
    factory { FetchPlaylistsUseCase(get(), get(), get(), get()) }
    factory { FetchCarouselItemsUseCase(get(), get(), get(), get(), get(), get(), get()) }
    factory { FetchHomeItemsUseCase(get(), get(), get()) }
    single { HomeNavigator(get()) }
    factory { PlaylistCreator(get(), get(), get()) }
    factory { LastSeenPlaylistCreator(get()) }
}