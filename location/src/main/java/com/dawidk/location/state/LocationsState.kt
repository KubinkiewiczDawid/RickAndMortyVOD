package com.dawidk.location.state

import androidx.paging.PagingData
import com.dawidk.common.mvi.ViewState
import com.dawidk.core.domain.model.Location

sealed class LocationsState: ViewState {
    object Loading : LocationsState()
    data class DataLoaded(val data: PagingData<Location>) : LocationsState()
    data class Error(val exception: Throwable) : LocationsState()
}