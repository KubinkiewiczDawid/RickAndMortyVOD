package com.dawidk.core.executors

import com.apollographql.apollo.exception.ApolloException
import com.dawidk.core.RickAndMortyAPI
import com.dawidk.core.domain.mappers.mapToDomainModel
import com.dawidk.core.domain.model.Location
import com.dawidk.core.utils.DataLoadingException

class FetchLocationByIdExecutor(private val rickAndMortyAPI: RickAndMortyAPI) {

    suspend fun getLocationById(id: String): Location {
        return try {
            rickAndMortyAPI.getLocationById(id)?.mapToDomainModel()
                ?: throw DataLoadingException("ID: $id")
        } catch (e: ApolloException) {
            throw DataLoadingException("")
        }
    }
}