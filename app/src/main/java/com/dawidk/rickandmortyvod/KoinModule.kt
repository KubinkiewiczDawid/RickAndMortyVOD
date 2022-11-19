package com.dawidk.rickandmortyvod

import com.dawidk.rickandmortyvod.navigation.MainActivityNavigatorHandler
import com.dawidk.rickandmortyvod.navigation.MainNavigator
import com.dawidk.rickandmortyvod.splashScreen.SplashViewModel
import com.dawidk.rickandmortyvod.usecase.CheckForNewEpisodeUseCase
import com.dawidk.common.navigation.MainActivityNavigator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MainViewModel() }
    viewModel { SplashViewModel(get(), get(), get(), get(), get(), get(), get()) }
    single { MainNavigator(get(), get()) }
    single<MainActivityNavigator> { MainActivityNavigatorHandler(get()) }
    single { CheckForNewEpisodeUseCase(get(), get()) }
}