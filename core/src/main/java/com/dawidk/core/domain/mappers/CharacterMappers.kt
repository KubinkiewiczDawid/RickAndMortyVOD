package com.dawidk.core.domain.mappers

import CharacterByIdQuery
import CharactersByIdsQuery
import CharactersListInfoQuery
import CharactersListQuery
import com.apollographql.apollo.api.toInput
import com.dawidk.core.domain.model.*
import fragment.CharacterDetails
import type.FilterCharacter

fun CharactersListQuery.Info.mapToDomainModel() = fragments.infoDetails.mapToDomainModel()

fun CharacterByIdQuery.Character.mapToDomainModel() = fragments.characterDetails.mapToDomainModel()

fun CharactersListQuery.Result.mapToDomainModel() = fragments.characterDetails.mapToDomainModel()

fun CharactersByIdsQuery.CharactersById.mapToDomainModel() =
    fragments.characterDetails.mapToDomainModel()

fun CharactersListInfoQuery.Info.mapToDomainModel() = fragments.infoDetails.mapToDomainModel()

fun CharactersListQuery.Data.mapToDomainModel(): CharacterResponse {
    val info = characters?.info
    val results = characters?.results
    return CharacterResponse(
        info = info?.mapToDomainModel() ?: Info.EMPTY,
        results = results?.mapNotNull { it?.mapToDomainModel() } ?: emptyList()
    )
}

fun CharacterDetails.Character.mapToDomainModel(): Character {
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

fun CharacterDetails.Episode.mapToDomainModel() = Episode(
    id = id ?: "",
    name = name ?: "",
    airDate = air_date ?: "",
    episode = episode ?: "",
    characters = characters.mapNotNull { it?.mapToDomainModel() },
    created = ""
)

fun CharacterDetails.Location.mapToDomainModel() = Location(
    id = id ?: "",
    name = name ?: "",
    type = type ?: "",
    dimension = dimension ?: "",
    residents = emptyList(),
    created = ""
)

fun CharacterFilter.mapToFilterCharacter() = FilterCharacter(
    name = name.toInput(),
    status = status.toInput(),
    species = species.toInput(),
    type = type.toInput(),
    gender = gender.toInput()
)

fun CharacterDetails.mapToDomainModel() = Character(
    id = id ?: "",
    name = name ?: "",
    status = CharacterStatus.fromStatus(status),
    species = species ?: "",
    type = type ?: "",
    gender = gender ?: "",
    origin = location?.mapToDomainModel() ?: Location.EMPTY,
    image = image ?: "",
    location = location?.mapToDomainModel() ?: Location.EMPTY,
    episode = episode.mapNotNull {
        it?.mapToDomainModel()
    },
    created = created ?: ""
)