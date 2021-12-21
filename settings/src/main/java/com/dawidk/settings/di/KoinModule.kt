package com.dawidk.settings.di

import com.dawidk.common.navigation.SettingsActivityNavigator
import com.dawidk.settings.SettingsViewModel
import com.dawidk.settings.navigation.SettingsActivityNavigatorHandler
import com.dawidk.settings.navigation.SettingsNavigator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel { SettingsViewModel(get()) }
    single { SettingsNavigator(get(), get()) }
    single<SettingsActivityNavigator> { SettingsActivityNavigatorHandler(get()) }
}