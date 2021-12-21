package com.dawidk.episodes.model

import com.dawidk.core.domain.model.Episode

data class EpisodesData(
    val chosenSeason: String = "all",
    val episodesMap: Map<String, List<Episode>>
) {

    companion object {

        val DEFAULT = EpisodesData(
            chosenSeason = "all",
            episodesMap = emptyMap()
        )
    }
}