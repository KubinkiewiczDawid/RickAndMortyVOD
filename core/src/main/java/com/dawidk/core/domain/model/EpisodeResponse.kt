package com.dawidk.core.domain.model

data class EpisodeResponse(
    val info: Info,
    val results: List<Episode>
) {

    companion object {

        val EMPTY = EpisodeResponse(
            info = Info(count = 0, pages = 0, next = null, prev = null),
            results = emptyList()
        )
    }
}