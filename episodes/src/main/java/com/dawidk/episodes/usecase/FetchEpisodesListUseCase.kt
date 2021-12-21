package com.dawidk.episodes.usecase

import android.content.Context
import com.dawidk.core.domain.model.Episode
import com.dawidk.core.executors.FetchEpisodesListExecutor
import com.dawidk.core.executors.FetchEpisodesListInfoExecutor
import com.dawidk.episodes.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchEpisodesListUseCase(
    private val context: Context,
    private val fetchEpisodesListExecutor: FetchEpisodesListExecutor,
    private val fetchEpisodesListInfoExecutor: FetchEpisodesListInfoExecutor
) {

    suspend operator fun invoke(): Flow<Map<String, List<Episode>>> = flow {
        val numberOfPages = fetchEpisodesListInfoExecutor.getEpisodesListInfo().pages
        val episodesList: MutableList<Episode> = emptyList<Episode>().toMutableList()

        repeat(numberOfPages) {
            episodesList += fetchEpisodesListExecutor.getEpisodesList(it + 1).results
        }
        val episodesMap = episodesList.groupBy {
            val re = Regex("E[0-9]*")
            val item = re.replace(it.episode, "")
            item
        }.toMutableMap()
        episodesMap[context.resources.getString(R.string.all)] = episodesList
        emit(episodesMap)
    }
}