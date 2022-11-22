package com.dawidk.home

import androidx.lifecycle.viewModelScope
import com.dawidk.common.mvi.BaseViewModel
import com.dawidk.home.model.Playlist
import com.dawidk.home.mvi.HomeAction
import com.dawidk.home.mvi.HomeEvent
import com.dawidk.home.mvi.HomeState
import com.dawidk.home.useCases.FetchHomeItemsUseCase
import kotlinx.coroutines.launch

class HomeViewModel(
    private val fetchHomeItemsUseCase: FetchHomeItemsUseCase,
) : BaseViewModel<HomeEvent, HomeAction, HomeState>(HomeState.Loading) {

    override fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.LoadHomeItems -> fetchHomeScreenData()
            is HomeAction.NavigateToSeeAllScreen -> navigateToSeeAllFragment(action.playlist)
            is HomeAction.NavigateToCharacterDetailsScreen -> navigateToCharacterDetails(action.id)
            is HomeAction.NavigateToEpisodeDetailsScreen -> navigateToEpisodeDetails(action.id)
            is HomeAction.NavigateToVideoPlayerScreen -> navigateToVideoPlayerScreen(action.episodeId)
        }
    }

    private fun fetchHomeScreenData() {
        viewModelScope.launch {
            fetchHomeItemsUseCase().collect {
                updateState(HomeState.DataLoaded(it.carouselItems + it.playlistItems))
            }
        }
    }

    private fun navigateToSeeAllFragment(playlist: Playlist) {
        emitEvent(HomeEvent.NavigateToSeeAllScreen(playlist))
    }

    private fun navigateToCharacterDetails(id: String) {
        emitEvent(HomeEvent.NavigateToCharacterDetailsScreen(id))
    }

    private fun navigateToEpisodeDetails(id: String) {
        emitEvent(HomeEvent.NavigateToEpisodeDetailsScreen(id))
    }

    private fun navigateToVideoPlayerScreen(episodeId: String) {
        emitEvent(HomeEvent.NavigateToVideoPlayerScreen(episodeId))
    }

}