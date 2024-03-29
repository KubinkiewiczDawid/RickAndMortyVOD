package com.dawidk.search.usecase

import com.dawidk.core.domain.model.CharacterFilter
import com.dawidk.core.domain.model.EpisodeFilter
import com.dawidk.core.domain.model.LocationFilter
import com.dawidk.core.executors.FetchCharactersListExecutor
import com.dawidk.core.executors.FetchEpisodesListExecutor
import com.dawidk.core.executors.FetchLocationsListExecutor
import com.dawidk.search.model.SearchItem
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

const val MIN_QUERY_LENGTH = 2

class SearchItemsUseCase(
    private val fetchCharactersListExecutor: FetchCharactersListExecutor,
    private val fetchLocationsListExecutor: FetchLocationsListExecutor,
    private val fetchEpisodesListExecutor: FetchEpisodesListExecutor
) {

    suspend operator fun invoke(searchQuery: String): Flow<List<SearchItem>> = flow {
        var foundItemsList = emptyList<SearchItem>()
        if (searchQuery.length > MIN_QUERY_LENGTH) {
            coroutineScope {
                val charactersListDeferred = async {
                    fetchCharactersListExecutor.getCharactersList(CharacterFilter(name = searchQuery))
                        .map {
                            val (id, name, _, _, _, _, _, _, image) = it
                            SearchItem.CharacterItem(id, name, image)
                        }
                }
                val locationsListDeferred = async {
                    fetchLocationsListExecutor.getLocationsList(
                        filter = LocationFilter(name = searchQuery)
                    ).results
                        .map {
                            SearchItem.LocationItem(it.id, it.name)
                        }
                }
                val episodesListDeferred = async {
                    fetchEpisodesListExecutor.getEpisodesList(
                        filter = EpisodeFilter(name = searchQuery)
                    ).results
                        .map {
                            SearchItem.EpisodeItem(it.id, it.name, it.episode, it.characters)
                        }
                }
                foundItemsList =
                    charactersListDeferred.await() + locationsListDeferred.await() + episodesListDeferred.await()
            }
        }
        emit(foundItemsList)
    }
}