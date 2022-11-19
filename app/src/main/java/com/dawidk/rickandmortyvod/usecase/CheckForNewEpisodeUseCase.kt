package com.dawidk.rickandmortyvod.usecase

import com.dawidk.core.datastore.EpisodesInfoDataStoreRepository
import com.dawidk.core.executors.FetchEpisodesListInfoExecutor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect

class CheckForNewEpisodeUseCase(
    private val episodesInfoDataStoreRepository: EpisodesInfoDataStoreRepository,
    private val fetchEpisodesListInfoExecutor: FetchEpisodesListInfoExecutor
) {

    suspend operator fun invoke(): Flow<Boolean> = flow {
        episodesInfoDataStoreRepository.episodesInfoFlow.collect {
            emit(it.episodesCount < fetchEpisodesListInfoExecutor.getEpisodesListInfo().count)
        }
    }
}