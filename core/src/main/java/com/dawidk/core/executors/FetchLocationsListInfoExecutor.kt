package com.dawidk.core.executors

import com.apollographql.apollo.exception.ApolloException
import com.dawidk.core.RickAndMortyAPI
import com.dawidk.core.domain.mappers.mapToDomainModel
import com.dawidk.core.domain.model.Info
import com.dawidk.core.domain.model.LocationResponse

class FetchLocationsListInfoExecutor(private val rickAndMortyAPI: RickAndMortyAPI) {

    suspend fun getLocationsListInfo(): Info {

        return try {
            rickAndMortyAPI.getLocationsListInfo()?.mapToDomainModel()
                ?: LocationResponse.EMPTY.info
        } catch (e: ApolloException) {
            LocationResponse.EMPTY.info
        }
    }
}