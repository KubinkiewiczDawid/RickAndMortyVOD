package com.dawidk.location.di

import com.dawidk.location.LocationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val locationModule = module {
    viewModel { LocationsViewModel(get()) }
}