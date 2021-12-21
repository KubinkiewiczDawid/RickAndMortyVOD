package com.dawidk.home.state

import com.dawidk.home.model.HomeItem

sealed class HomeState {
    data class DataLoaded(val homeItems: List<HomeItem>) : HomeState()
    data class Error(val exception: Throwable) : HomeState()
    object Loading : HomeState()
}