package com.dawidk.core.executors

import com.apollographql.apollo.exception.ApolloException
import com.dawidk.core.RickAndMortyAPI
import com.dawidk.core.domain.mappers.mapToDomainModel
import com.dawidk.core.domain.model.CharacterResponse
import com.dawidk.core.domain.model.Info

class FetchCharactersListInfoExecutor(private val rickAndMortyAPI: RickAndMortyAPI) {

    suspend fun getCharactersListInfo(): Info {

        return try {
            rickAndMortyAPI.getCharactersListInfo()?.mapToDomainModel()
                ?: CharacterResponse.EMPTY.info
        } catch (e: ApolloException) {
            CharacterResponse.EMPTY.info
        }
    }
}