package com.dawidk.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawidk.home.model.Playlist
import com.dawidk.home.state.HomeAction
import com.dawidk.home.state.HomeEvent
import com.dawidk.home.state.HomeState
import com.dawidk.home.useCases.FetchHomeItemsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val fetchHomeItemsUseCase: FetchHomeItemsUseCase,
) : ViewModel() {

    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState.Loading)
    val state: StateFlow<HomeState> = _state
    private val _event: MutableSharedFlow<HomeEvent> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<HomeEvent> = _event

    fun onAction(action: HomeAction) {
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
                _state.value = HomeState.DataLoaded(it.carouselItems + it.playlistItems)
            }
        }
    }

    private fun navigateToSeeAllFragment(playlist: Playlist) {
        viewModelScope.launch {
            _event.tryEmit(HomeEvent.NavigateToSeeAllScreen(playlist))
        }
    }

    private fun navigateToCharacterDetails(id: String) {
        viewModelScope.launch {
            _event.tryEmit(HomeEvent.NavigateToCharacterDetailsScreen(id))
        }
    }

    private fun navigateToEpisodeDetails(id: String) {
        viewModelScope.launch {
            _event.tryEmit(HomeEvent.NavigateToEpisodeDetailsScreen(id))
        }
    }

    private fun navigateToVideoPlayerScreen(episodeId: String) {
        viewModelScope.launch {
            _event.tryEmit(HomeEvent.NavigateToVideoPlayerScreen(episodeId))
        }
    }

}