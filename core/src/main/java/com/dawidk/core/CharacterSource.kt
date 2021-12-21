package com.dawidk.core

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dawidk.core.domain.model.Character
import com.dawidk.core.executors.FetchCharactersListExecutor

class CharacterSource(private val fetchCharactersListExecutor: FetchCharactersListExecutor) :
    PagingSource<Int, Character>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        return try {
            val page = params.key ?: 1
            val response = fetchCharactersListExecutor.getCharactersList(page)

            LoadResult.Page(
                data = response.results,
                prevKey = response.info.prev,
                nextKey = response.info.next
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? = state.anchorPosition
}

