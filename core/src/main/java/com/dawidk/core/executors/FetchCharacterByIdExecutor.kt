package com.dawidk.core.executors

import com.apollographql.apollo.exception.ApolloException
import com.dawidk.core.RickAndMortyAPI
import com.dawidk.core.domain.mappers.mapToDomainModel
import com.dawidk.core.domain.model.Character
import com.dawidk.core.utils.DataLoadingException

class FetchCharacterByIdExecutor(private val rickAndMortyAPI: RickAndMortyAPI) {

    suspend fun getCharacterById(id: String): Character {
        return try {
            rickAndMortyAPI.getCharacterById(id)?.mapToDomainModel()
                ?: throw DataLoadingException("ID: $id")
        } catch (e: ApolloException) {
            throw DataLoadingException("Error fetching character")
        }
    }
}