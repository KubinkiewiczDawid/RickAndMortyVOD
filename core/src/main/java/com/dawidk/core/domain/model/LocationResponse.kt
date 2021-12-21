package com.dawidk.core.domain.model

data class LocationResponse(
    val info: Info,
    val results: List<Location>
) {

    companion object {

        val EMPTY = LocationResponse(
            info = Info(count = 0, pages = 0, next = null, prev = null),
            results = emptyList()
        )
    }
}