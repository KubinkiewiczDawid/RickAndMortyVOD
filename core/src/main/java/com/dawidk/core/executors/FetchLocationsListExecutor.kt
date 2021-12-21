package com.dawidk.core.executors

import com.apollographql.apollo.exception.ApolloException
import com.dawidk.core.RickAndMortyAPI
import com.dawidk.core.domain.mappers.mapToDomainModel
import com.dawidk.core.domain.mappers.mapToFilterLocation
import com.dawidk.core.domain.model.LocationFilter
import com.dawidk.core.domain.model.LocationResponse
import com.dawidk.core.utils.DataLoadingException

class FetchLocationsListExecutor(private val rickAndMortyAPI: RickAndMortyAPI) {

    suspend fun getLocationsList(page: Int?): LocationResponse {
        try {
            return rickAndMortyAPI.getLocationsList(page)?.mapToDomainModel()
                ?: LocationResponse.EMPTY
        } catch (e: ApolloException) {
            throw DataLoadingException("")
        }
    }

    suspend fun getLocationsList(page: Int? = null, filter: LocationFilter): LocationResponse {
        try {
            return rickAndMortyAPI.getLocationsList(page, filter.mapToFilterLocation())
                ?.mapToDomainModel() ?: LocationResponse.EMPTY
        } catch (e: ApolloException) {
            throw DataLoadingException("")
        }
    }
}