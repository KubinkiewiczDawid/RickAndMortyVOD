package com.dawidk.core.executors

import com.apollographql.apollo.exception.ApolloException
import com.dawidk.core.RickAndMortyAPI
import com.dawidk.core.domain.mappers.mapToDomainModel
import com.dawidk.core.domain.mappers.mapToFilterEpisode
import com.dawidk.core.domain.model.EpisodeFilter
import com.dawidk.core.domain.model.EpisodeResponse
import com.dawidk.core.utils.DataLoadingException

class FetchEpisodesListExecutor(private val rickAndMortyAPI: RickAndMortyAPI) {

    suspend fun getEpisodesList(page: Int?): EpisodeResponse {
        return try {
            rickAndMortyAPI.getEpisodesList(page = page)?.mapToDomainModel()
                ?: EpisodeResponse.EMPTY
        } catch (e: ApolloException) {
            throw DataLoadingException("Error fetching episodes")
        }
    }

    suspend fun getEpisodesList(page: Int? = null, filter: EpisodeFilter): EpisodeResponse {
        return try {
            rickAndMortyAPI.getEpisodesList(page = page, filter = filter.mapToFilterEpisode())
                ?.mapToDomainModel() ?: EpisodeResponse.EMPTY
        } catch (e: ApolloException) {
            throw DataLoadingException("Error fetching episodes")
        }
    }
}