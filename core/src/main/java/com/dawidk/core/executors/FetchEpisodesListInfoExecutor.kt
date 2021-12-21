package com.dawidk.core.executors

import com.apollographql.apollo.exception.ApolloException
import com.dawidk.core.RickAndMortyAPI
import com.dawidk.core.domain.mappers.mapToDomainModel
import com.dawidk.core.domain.mappers.mapToFilterEpisode
import com.dawidk.core.domain.model.EpisodeFilter
import com.dawidk.core.domain.model.EpisodeResponse
import com.dawidk.core.domain.model.Info

class FetchEpisodesListInfoExecutor(private val rickAndMortyAPI: RickAndMortyAPI) {

    suspend fun getEpisodesListInfo(): Info {
        return try {
            rickAndMortyAPI.getEpisodesListInfo()?.mapToDomainModel()
                ?: EpisodeResponse.EMPTY.info
        } catch (e: ApolloException) {
            EpisodeResponse.EMPTY.info
        }
    }

    suspend fun getEpisodesListInfo(filter: EpisodeFilter): Info {
        return try {
            rickAndMortyAPI.getEpisodesListInfo(filter.mapToFilterEpisode())
                ?.mapToDomainModel() ?: EpisodeResponse.EMPTY.info
        } catch (e: ApolloException) {
            EpisodeResponse.EMPTY.info
        }
    }
}