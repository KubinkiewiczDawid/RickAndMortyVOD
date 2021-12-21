package com.dawidk.rickandmortyvod.splashScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawidk.rickandmortyvod.splashScreen.state.SplashAction
import com.dawidk.rickandmortyvod.splashScreen.state.SplashState
import com.dawidk.core.datastore.EpisodesInfoDataStoreRepository
import com.dawidk.core.datastore.HomeScreenDataStoreRepository
import com.dawidk.core.datastore.UserCredentialsDataStoreRepository
import com.dawidk.core.domain.mappers.mapToAccountInfo
import com.dawidk.core.executors.FetchEpisodesListInfoExecutor
import com.dawidk.core.executors.UpdateCacheExecutor
import com.dawidk.core.utils.DataLoadingException
import com.dawidk.home.useCases.FetchCarouselItemsUseCase
import com.dawidk.home.useCases.FetchPlaylistsUseCase
import com.dawidk.home.useCases.TIMESTAMP_DURATION
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

class SplashViewModel(
    private val userCredentialsDataStoreRepository: UserCredentialsDataStoreRepository,
    private val episodesInfoDataStoreRepository: EpisodesInfoDataStoreRepository,
    private val updateCacheExecutor: UpdateCacheExecutor,
    private val fetchEpisodesListInfoExecutor: FetchEpisodesListInfoExecutor,
    private val homeScreenDataStoreRepository: HomeScreenDataStoreRepository,
    private val fetchPlaylistsUseCase: FetchPlaylistsUseCase,
    private val fetchCarouselItemsUseCase: FetchCarouselItemsUseCase,
) : ViewModel() {

    private val _state: MutableStateFlow<SplashState> =
        MutableStateFlow(SplashState.Loading)
    val state: StateFlow<SplashState> = _state

    fun onAction(action: SplashAction) {
        when (action) {
            is SplashAction.Init -> loadUserData()
        }
    }

    suspend fun updateCache() {
        val episodesPages = fetchEpisodesListInfoExecutor.getEpisodesListInfo().pages

        val homeScreenContents = homeScreenDataStoreRepository.homeScreenFlow.first()
        if (homeScreenContents.playlistsList.isEmpty()
            || isTimestampOutdated(homeScreenContents.timestamp)
        ) {
            fetchPlaylistsUseCase.generatePlaylists()
            fetchCarouselItemsUseCase.generateCarousels()
            homeScreenDataStoreRepository.setTimestamp(timestamp = Date().time)
        }

        try {
            updateCacheExecutor.updateCache(
                episodesPages,
                homeScreenDataStoreRepository.homeScreenFlow.first()
            )
        } catch (ex: DataLoadingException) {
            _state.value = SplashState.Error(DataLoadingException("Network error"))
        }
    }

    suspend fun saveEpisodesCount() {
        val episodesCount = fetchEpisodesListInfoExecutor.getEpisodesListInfo().count
        
        episodesInfoDataStoreRepository.updateEpisodesCount(
            episodesCount
        )
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

    private fun loadUserData() {
        viewModelScope.launch {
            userCredentialsDataStoreRepository.userCredentialsFlow.collect { userCredentials ->
                val credentials = userCredentials.mapToAccountInfo()
                _state.value = SplashState.Loading
                if (credentials.email.isNotBlank())
                    _state.value = SplashState.SignedIn
                else
                    _state.value = SplashState.NotSignedIn
            }
        }
    }
}