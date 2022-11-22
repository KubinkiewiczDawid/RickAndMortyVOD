package com.dawidk.home.mvi

import com.dawidk.common.mvi.ViewState
import com.dawidk.home.model.HomeItem

sealed class HomeState : ViewState {
    data class DataLoaded(val homeItems: List<HomeItem>) : HomeState()
    data class Error(val exception: Throwable) : HomeState()
    object Loading : HomeState()
}