package com.dawidk.core.executors

import com.apollographql.apollo.exception.ApolloException
import com.dawidk.core.RickAndMortyAPI
import com.dawidk.core.domain.mappers.mapToDomainModel
import com.dawidk.core.domain.model.Episode
import com.dawidk.core.utils.DataLoadingException

class FetchEpisodeByIdExecutor(private val rickAndMortyAPI: RickAndMortyAPI) {

    suspend fun getEpisodeById(id: String): Episode {
        return try {
            rickAndMortyAPI.getEpisodeById(id)?.mapToDomainModel()
                ?: throw DataLoadingException("ID: $id")
        } catch (e: ApolloException) {
            throw DataLoadingException("Error fetching episode")
        }
    }
}