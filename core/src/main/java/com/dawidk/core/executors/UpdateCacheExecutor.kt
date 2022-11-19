package com.dawidk.core.executors

import com.apollographql.apollo.exception.ApolloException
import com.dawidk.core.RickAndMortyAPI
import com.dawidk.core.datastore.HomeScreenContents
import com.dawidk.core.utils.DataLoadingException

class UpdateCacheExecutor(private val rickAndMortyAPI: RickAndMortyAPI) {

    suspend fun updateCache(episodesPages: Int, homeScreenContents: HomeScreenContents) {
        try {
            rickAndMortyAPI.updateCache(episodesPages, homeScreenContents)
        } catch (e: ApolloException) {
            throw DataLoadingException("Error writing to cache")
        }
    }
}