package com.dawidk.core.executors

import com.apollographql.apollo.exception.ApolloException
import com.dawidk.core.RickAndMortyAPI
import com.dawidk.core.domain.mappers.mapToDomainModel
import com.dawidk.core.domain.mappers.mapToFilterCharacter
import com.dawidk.core.domain.model.Character
import com.dawidk.core.domain.model.CharacterFilter
import com.dawidk.core.domain.model.CharacterResponse
import com.dawidk.core.utils.DataLoadingException

class FetchCharactersListExecutor(private val rickAndMortyAPI: RickAndMortyAPI) {

    suspend fun getCharactersList(page: Int?): CharacterResponse {
        return try {
            rickAndMortyAPI.getCharactersList(page = page)?.mapToDomainModel()
                ?: CharacterResponse.EMPTY
        } catch (e: ApolloException) {
            throw DataLoadingException("Error fetching characters")
        }
    }

    suspend fun getCharactersList(filter: CharacterFilter): List<Character> {
        return try {
            rickAndMortyAPI.getCharactersList(filter = filter.mapToFilterCharacter())
                ?.mapNotNull { it?.mapToDomainModel() } ?: emptyList()
        } catch (e: ApolloException) {
            throw DataLoadingException("Error fetching characters")
        }
    }
}