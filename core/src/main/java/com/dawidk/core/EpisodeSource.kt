package com.dawidk.core

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dawidk.core.domain.model.Episode
import com.dawidk.core.domain.model.EpisodeFilter
import com.dawidk.core.executors.FetchEpisodesListExecutor

class EpisodeSource(
    private val fetchEpisodesListExecutor: FetchEpisodesListExecutor,
    private val episodeFilter: EpisodeFilter? = null
) : PagingSource<Int, Episode>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Episode> {
        return try {
            val page = params.key ?: 1
            val response = if (episodeFilter == null)
                fetchEpisodesListExecutor.getEpisodesList(page)
            else
                fetchEpisodesListExecutor.getEpisodesList(page, episodeFilter)

            LoadResult.Page(
                data = response.results,
                prevKey = response.info.prev,
                nextKey = response.info.next
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Episode>): Int? = state.anchorPosition
}