package com.dawidk.core

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dawidk.core.domain.model.Location
import com.dawidk.core.domain.model.LocationFilter
import com.dawidk.core.executors.FetchLocationsListExecutor

class LocationSource(
    private val fetchLocationsListExecutor: FetchLocationsListExecutor,
    private val locationFilter: LocationFilter? = null
) : PagingSource<Int, Location>() {

    @Throws(Exception::class)
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Location> {
        return try {
            val pageNumber = params.key ?: 1
            val response =
                if (locationFilter == null)
                    fetchLocationsListExecutor.getLocationsList(pageNumber)
                else
                    fetchLocationsListExecutor.getLocationsList(pageNumber, locationFilter)
            val result = response.results

            LoadResult.Page(
                data = result,
                prevKey = response.info.prev,
                nextKey = response.info.next
            )
        } catch (e: Exception) {
            LoadResult.Error(Exception("Cannot fetch locations list"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Location>): Int? = state.anchorPosition
}