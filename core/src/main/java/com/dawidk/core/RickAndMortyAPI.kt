package com.dawidk.core

import CharacterByIdQuery
import CharactersByIdsQuery
import CharactersListInfoQuery
import CharactersListQuery
import EpisodeByIdQuery
import EpisodesListInfoQuery
import EpisodesListQuery
import LocationByIdQuery
import LocationsListInfoQuery
import LocationsListQuery
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.api.toInput
import com.apollographql.apollo.coroutines.await
import com.dawidk.core.datastore.HomeScreenContents
import com.dawidk.core.datastore.HomeScreenItem
import type.FilterCharacter
import type.FilterEpisode
import type.FilterLocation

private const val PAGE_NUMBER = 1

class RickAndMortyAPI(private val apolloContext: ApolloContext) {

    suspend fun updateCache(episodesPages: Int, homeScreenContents: HomeScreenContents) {
        getCharactersList(PAGE_NUMBER)
        getLocationsList(PAGE_NUMBER)
        IntRange(1, episodesPages).map {
            getEpisodesList(it)
        }

        (homeScreenContents.playlistsList + homeScreenContents.carouselList).flatMap { it.itemsList }
            .forEach { item ->
                when (item.type) {
                    HomeScreenItem.ItemType.CHARACTER -> {
                        getCharacterById(item.itemId)
                    }
                    HomeScreenItem.ItemType.EPISODE -> {
                        getEpisodeById(item.itemId)
                    }
                    else -> {
                        getLocationById(item.itemId)
                    }
                }
            }
    }

    internal suspend fun getCharactersList(page: Int?): CharactersListQuery.Data? {
        return apolloContext.apolloClient.query(CharactersListQuery(page.toInput()))
            .toBuilder()
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .build().await().data
    }

    internal suspend fun getLocationsList(page: Int?): LocationsListQuery.Data? {
        return apolloContext.apolloClient.query(LocationsListQuery(page.toInput()))
            .toBuilder()
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .build().await().data
    }

    internal suspend fun getEpisodesList(page: Int?): EpisodesListQuery.Data? {
        return apolloContext.apolloClient.query(EpisodesListQuery(page.toInput()))
            .toBuilder()
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .build().await().data
    }

    internal suspend fun getCharacterById(id: String): CharacterByIdQuery.Character? {
        return apolloContext.apolloClient.query(CharacterByIdQuery(id))
            .toBuilder()
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .build().await().data?.character
    }

    internal suspend fun getCharactersList(filter: FilterCharacter): List<CharactersListQuery.Result?>? {
        val response = apolloContext.apolloClient.query(
            CharactersListQuery(
                page = 1.toInput(),
                filter = filter.toInput()
            )
        ).await()
        return response.data?.characters?.results
    }

    internal suspend fun getCharactersByIds(ids: List<String>): List<CharactersByIdsQuery.CharactersById?>? {
        val response = apolloContext.apolloClient.query(CharactersByIdsQuery(ids)).await()
        return response.data?.charactersByIds
    }

    internal suspend fun getCharactersListInfo(): CharactersListInfoQuery.Info? {
        val response = apolloContext.apolloClient.query(CharactersListInfoQuery()).await()
        return response.data?.characters?.info
    }

    internal suspend fun getLocationById(id: String): LocationByIdQuery.Location? {
        return apolloContext.apolloClient.query(LocationByIdQuery(id))
            .toBuilder()
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .build().await().data?.location
    }

    internal suspend fun getLocationsList(
        page: Int?,
        filter: FilterLocation
    ): LocationsListQuery.Data? {
        val response = apolloContext.apolloClient.query(
            LocationsListQuery(
                page = page.toInput(),
                filter = filter.toInput()
            )
        ).await()
        return response.data
    }

    internal suspend fun getLocationsListInfo(): LocationsListInfoQuery.Info? {
        val response = apolloContext.apolloClient.query(LocationsListInfoQuery()).await()
        return response.data?.locations?.info
    }

    internal suspend fun getEpisodeById(id: String): EpisodeByIdQuery.Episode? {
        return apolloContext.apolloClient.query(EpisodeByIdQuery(id))
            .toBuilder()
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .build().await().data?.episode
    }

    internal suspend fun getEpisodesListInfo(): EpisodesListInfoQuery.Info? {
        val response = apolloContext.apolloClient.query(EpisodesListInfoQuery()).await()
        return response.data?.episodes?.info
    }

    internal suspend fun getEpisodesListInfo(filter: FilterEpisode): EpisodesListInfoQuery.Info? {
        val response =
            apolloContext.apolloClient.query(EpisodesListInfoQuery(filter.toInput())).await()
        return response.data?.episodes?.info
    }

    internal suspend fun getEpisodesList(
        page: Int?,
        filter: FilterEpisode
    ): EpisodesListQuery.Data? {
        val response = apolloContext.apolloClient.query(
            EpisodesListQuery(
                page = page.toInput(),
                filter = filter.toInput()
            )
        ).await()
        return response.data
    }
}