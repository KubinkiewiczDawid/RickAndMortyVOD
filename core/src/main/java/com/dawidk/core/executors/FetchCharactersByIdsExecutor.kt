package com.dawidk.core.executors

import com.apollographql.apollo.exception.ApolloException
import com.dawidk.core.RickAndMortyAPI
import com.dawidk.core.domain.mappers.mapToDomainModel
import com.dawidk.core.domain.model.Character
import com.dawidk.core.utils.DataLoadingException

class FetchCharactersByIdsExecutor(private val rickAndMortyAPI: RickAndMortyAPI) {

    suspend fun getCharactersByIds(ids: List<String>): List<Character> {
        return try {
            rickAndMortyAPI.getCharactersByIds(ids)?.mapNotNull { it?.mapToDomainModel() }
                ?: emptyList()
        } catch (e: ApolloException) {
            throw DataLoadingException("Error fetching characters")
        }
    }
}