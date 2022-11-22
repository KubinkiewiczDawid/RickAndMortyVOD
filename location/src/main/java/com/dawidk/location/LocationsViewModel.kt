package com.dawidk.location

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dawidk.common.mvi.BaseViewModel
import com.dawidk.core.LocationSource
import com.dawidk.core.domain.model.Location
import com.dawidk.core.executors.FetchLocationsListExecutor
import com.dawidk.location.state.LocationsAction
import com.dawidk.location.state.LocationsEvent
import com.dawidk.location.state.LocationsState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

class LocationsViewModel(
    private val fetchLocationsListExecutor: FetchLocationsListExecutor
) : BaseViewModel<LocationsEvent, LocationsAction, LocationsState>(LocationsState.Loading) {

    private var pagingData: PagingData<Location> = PagingData.empty()

    override fun onAction(action: LocationsAction) {
        when (action) {
            LocationsAction.Init -> loadLocations()
            LocationsAction.Load -> updateState(LocationsState.Loading)
            is LocationsAction.DataLoaded -> {
                if (state.value is LocationsState.Loading) {
                    updateState(LocationsState.DataLoaded(pagingData))
                }
            }
            is LocationsAction.HandleError -> updateState(LocationsState.Error(action.error))
        }
    }

    private fun loadLocations() {
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                LocationSource(fetchLocationsListExecutor)
            }.flow
                .cachedIn(viewModelScope).collectLatest {
                    pagingData = it
                    updateState(LocationsState.DataLoaded(it))
                }
        }
    }
}