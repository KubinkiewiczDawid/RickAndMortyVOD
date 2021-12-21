package com.dawidk.rickandmortyvod

import com.dawidk.rickandmortyvod.usecase.CheckForNewEpisodeUseCase
import com.dawidk.core.datastore.EpisodesInfoDataStoreRepository
import com.dawidk.core.domain.model.Info
import com.dawidk.core.executors.FetchEpisodesListInfoExecutor
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class NewEpisodeCheckTest {

    private val episodesInfoDataStoreRepository =
        mockk<EpisodesInfoDataStoreRepository>(relaxed = true)
    private val fetchEpisodesListInfoExecutor =
        mockk<FetchEpisodesListInfoExecutor>(relaxed = true)
    private val checkForNewEpisodeUseCase = CheckForNewEpisodeUseCase(
        episodesInfoDataStoreRepository, fetchEpisodesListInfoExecutor
    )

    @Test
    fun `should return true when stored episodes count is smaller than fetched`() {
        runBlocking {
            val storedEpisodesCount = 41
            val fetchedEpisodesCount = 42
            //given
            val dataStoreResponse = flow {
                emit(EpisodesInfo.newBuilder().setEpisodesCount(storedEpisodesCount).build())
            }
            val episodeInfoResponse = Info(fetchedEpisodesCount, 3, null, 2)
            //when
            coEvery { episodesInfoDataStoreRepository.episodesInfoFlow } returns dataStoreResponse
            coEvery { fetchEpisodesListInfoExecutor.getEpisodesListInfo() } returns episodeInfoResponse
            //then
            checkForNewEpisodeUseCase().collect { isNewEpisode ->
                assertEquals(true, isNewEpisode)
            }
        }
    }

    @Test
    fun `should return false when stored episodes count equals fetched`() {
        runBlocking {
            val STORED_EPISODES_COUNT = 42
            val FETCHED_EPISODES_COUNT = 42
            //given
            val dataStoreResponse = flow {
                emit(EpisodesInfo.newBuilder().setEpisodesCount(STORED_EPISODES_COUNT).build())
            }
            val episodeInfoResponse = Info(FETCHED_EPISODES_COUNT, 3, null, 2)

            coEvery { episodesInfoDataStoreRepository.episodesInfoFlow } returns dataStoreResponse
            coEvery { fetchEpisodesListInfoExecutor.getEpisodesListInfo() } returns episodeInfoResponse
            checkForNewEpisodeUseCase().collect { isNewEpisode ->
                assertEquals(false, isNewEpisode)
            }
        }
    }
}