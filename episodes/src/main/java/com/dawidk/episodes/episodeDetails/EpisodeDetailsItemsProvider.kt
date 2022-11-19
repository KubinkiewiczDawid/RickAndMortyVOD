package com.dawidk.episodes.episodeDetails

import com.dawidk.core.domain.model.Episode
import com.dawidk.episodes.model.EpisodeItem

class EpisodeDetailsItemsProvider {

    operator fun invoke(episode: Episode): List<EpisodeItem> {
        return insertHeaderData(episode) +
                episode.characters.map {
                    EpisodeItem.EpisodeCharacterItem(
                        character = it
                    )
                }
    }

    private fun insertHeaderData(episode: Episode): List<EpisodeItem> {
        return listOf(
            with(episode) {
                EpisodeItem.EpisodeInfoItem(
                    name = name,
                    air_date = airDate,
                    characters = episode.characters,
                    season = this.episode
                )
            },
            EpisodeItem.EpisodeTabItem
        )
    }
}