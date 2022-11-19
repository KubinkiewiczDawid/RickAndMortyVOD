package com.dawidk.home.useCases

import com.dawidk.core.datastore.HomeScreenDataStoreRepository
import com.dawidk.home.model.HomeItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.util.*
import java.util.concurrent.TimeUnit

const val TIMESTAMP_DURATION: Long = 24

class FetchHomeItemsUseCase(
    private val fetchPlaylistsUseCase: FetchPlaylistsUseCase,
    private val fetchCarouselItemsUseCase: FetchCarouselItemsUseCase,
    private val homeScreenDataStoreRepository: HomeScreenDataStoreRepository
) {

    operator fun invoke(): Flow<HomeItems> = flow {
        homeScreenDataStoreRepository.homeScreenFlow.collect {
            fetchCarouselItemsUseCase().combine(fetchPlaylistsUseCase()) { carouselItems, playlistItems ->
                HomeItems(carouselItems, playlistItems)
            }.collect { homeItems ->
                emit(homeItems)
            }
        }
    }

    private fun isTimestampOutdated(timestamp: Long): Boolean {
        return when (timestamp) {
            0.toLong() -> {
                false
            }
            else -> {
                Date().time > (timestamp + TimeUnit.HOURS.toMillis(TIMESTAMP_DURATION))
            }
        }
    }
}