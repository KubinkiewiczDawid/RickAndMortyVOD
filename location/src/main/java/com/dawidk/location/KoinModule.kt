package com.dawidk.location

import com.dawidk.location.ui.LocationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val locationModule = module {
    viewModel { LocationsViewModel(get()) }
}