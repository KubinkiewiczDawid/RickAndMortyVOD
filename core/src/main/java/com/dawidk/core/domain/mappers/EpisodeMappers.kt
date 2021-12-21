package com.dawidk.core.domain.mappers

import EpisodeByIdQuery
import EpisodesListInfoQuery
import EpisodesListQuery
import com.apollographql.apollo.api.toInput
import com.dawidk.core.domain.model.*
import fragment.EpisodeDetails
import type.FilterEpisode

fun EpisodeByIdQuery.Episode.mapToDomainModel() = fragments.episodeDetails.mapToDomainModel()

fun EpisodesListQuery.Info.mapToDomainModel() = fragments.infoDetails.mapToDomainModel()

fun EpisodesListQuery.Result.mapToDomainModel() = fragments.episodeDetails.mapToDomainModel()

fun EpisodesListInfoQuery.Info.mapToDomainModel() = fragments.infoDetails.mapToDomainModel()

fun EpisodesListQuery.Data.mapToDomainModel(): EpisodeResponse {
    val info = episodes?.info
    val results = episodes?.results
    return EpisodeResponse(
        info = info?.mapToDomainModel() ?: Info.EMPTY,
        results = results?.mapNotNull { it?.mapToDomainModel() } ?: emptyList()
    )
}

fun EpisodeFilter.mapToFilterEpisode() = FilterEpisode(
    name = name.toInput(),
    episode = episode.toInput()
)

fun EpisodeDetails.Character.mapToDomainModel(): Character {
    return Character(
        id = id ?: "",
        name = name ?: "",
        status = CharacterStatus.fromStatus(""),
        species = "",
        type = "",
        gender = "",
        origin = Location.EMPTY,
        image = image ?: "",
        location = Location.EMPTY,
        episode = emptyList(),
        created = ""
    )
}

fun EpisodeDetails.mapToDomainModel() = Episode(
    id = id ?: "",
    name = name ?: "",
    airDate = air_date ?: "",
    episode = episode ?: "",
    characters = characters.mapNotNull {
        it?.mapToDomainModel()
    },
    created = ""
)