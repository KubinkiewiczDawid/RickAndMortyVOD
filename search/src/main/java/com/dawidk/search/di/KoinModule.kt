package com.dawidk.search.di

import com.dawidk.search.SearchViewModel
import com.dawidk.search.navigation.SearchNavigator
import com.dawidk.search.usecase.SearchItemsUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {
    viewModel { SearchViewModel(get()) }
    single { SearchItemsUseCase(get(), get(), get()) }
    single { SearchNavigator() }
}