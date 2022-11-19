package com.dawidk.location.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dawidk.core.LocationSource
import com.dawidk.core.domain.model.Location
import com.dawidk.core.executors.FetchLocationsListExecutor
import com.dawidk.location.state.LocationsAction
import com.dawidk.location.state.LocationsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

class LocationsViewModel(
    private val fetchLocationsListExecutor: FetchLocationsListExecutor
) : ViewModel() {

    private val _state: MutableStateFlow<LocationsState> =
        MutableStateFlow(LocationsState.Loading)
    val state: StateFlow<LocationsState> = _state
    private var pagingData: PagingData<Location> = PagingData.empty()

    fun onAction(action: LocationsAction) {
        when (action) {
            LocationsAction.Init -> loadLocations()
            LocationsAction.Load -> _state.value = LocationsState.Loading
            is LocationsAction.DataLoaded -> {
                if (_state.value is LocationsState.Loading) {
                    _state.value = LocationsState.DataLoaded(pagingData)
                }
            }
            is LocationsAction.HandleError -> _state.value = LocationsState.Error(action.error)
        }
    }

    private fun loadLocations() {
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                LocationSource(fetchLocationsListExecutor)
            }.flow
                .cachedIn(viewModelScope).collectLatest {
                    pagingData = it
                    _state.value = LocationsState.DataLoaded(it)
                }
        }
    }
}