package com.dawidk.core.domain.mappers

import LocationByIdQuery
import LocationsListInfoQuery
import LocationsListQuery
import com.apollographql.apollo.api.toInput
import com.dawidk.core.domain.model.*
import fragment.LocationDetails
import type.FilterLocation

fun LocationByIdQuery.Location.mapToDomainModel() = fragments.locationDetails.mapToDomainModel()

fun LocationsListQuery.Info.mapToDomainModel() = fragments.infoDetails.mapToDomainModel()

fun LocationsListQuery.Result.mapToDomainModel() = fragments.locationDetails.mapToDomainModel()

fun LocationsListInfoQuery.Info.mapToDomainModel() = fragments.infoDetails.mapToDomainModel()

fun LocationsListQuery.Data.mapToDomainModel(): LocationResponse {
    val info = locations?.info
    val results = locations?.results
    return LocationResponse(
        info = info?.mapToDomainModel() ?: Info.EMPTY,
        results = results?.mapNotNull { it?.mapToDomainModel() } ?: emptyList()
    )
}

fun LocationDetails.Resident.mapToDomainModel() = Character(
    id = id ?: "",
    name = name ?: "",
    status = CharacterStatus.fromStatus(""),
    species = "",
    type = "",
    gender = "",
    origin = Location.EMPTY,
    image = "",
    location = Location.EMPTY,
    episode = emptyList(),
    created = ""
)

fun LocationFilter.mapToFilterLocation() = FilterLocation(
    name = name.toInput(),
    type = type.toInput(),
    dimension = dimension.toInput()
)

fun LocationDetails.mapToDomainModel() = Location(
    id = id ?: "",
    name = name ?: "",
    type = type ?: "",
    dimension = dimension ?: "",
    residents = residents.mapNotNull {
        it?.mapToDomainModel()
    },
    created = created ?: ""
)