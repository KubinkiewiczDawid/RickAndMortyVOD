package com.dawidk.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.mvi.BaseFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.home.databinding.HomeFragmentBinding
import com.dawidk.home.model.CarouselItem
import com.dawidk.home.model.Playlist
import com.dawidk.home.model.PlaylistItem
import com.dawidk.home.navigation.HomeNavigator
import com.dawidk.home.navigation.Screen
import com.dawidk.home.mvi.HomeAction
import com.dawidk.home.mvi.HomeEvent
import com.dawidk.home.mvi.HomeState
import com.dawidk.videoplayer.cast.service.CastVideoService
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment :
    BaseFragment<HomeEvent, HomeAction, HomeState, HomeViewModel, HomeFragmentBinding>(R.layout.home_fragment) {

    override val viewModel by viewModel<HomeViewModel>()
    override val binding by viewBinding(HomeFragmentBinding::bind)
    override val networkMonitor: NetworkMonitor by inject()
    private val homeNavigator: HomeNavigator by inject()
    private var homeScreenAdapter: HomeScreenAdapter? = null
    private val castVideoService: CastVideoService by inject()

    override fun onResume() {
        super.onResume()
        viewModel.onAction(HomeAction.LoadHomeItems)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeNavigator.navController = findNavController()

        homeScreenAdapter = HomeScreenAdapter()

        setUpRecyclerView()
        registerCarouselClickEvent()
        registerPlaylistClickEventListener()
        registerSeeAllClickEventListener()
    }

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.NavigateToSeeAllScreen -> navigateToSeeAllFragment(
                event.playlist
            )
            is HomeEvent.NavigateToCharacterDetailsScreen -> navigateToCharacterDetails(
                event.id
            )
            is HomeEvent.NavigateToEpisodeDetailsScreen -> navigateToEpisodeDetails(
                event.id
            )
            is HomeEvent.NavigateToVideoPlayerScreen -> navigateToVideoPlayerScreen(event.episodeId)
        }
    }

    override fun handleState(state: HomeState) {
        when (state) {
            is HomeState.DataLoaded -> updateUI(state)
            is HomeState.Error -> showError(state)
            is HomeState.Loading -> showLoading()
        }
    }

    override fun onDataLoadingException() {
        viewModel.onAction(HomeAction.LoadHomeItems)
    }

    override fun onDestroyView() {
        homeScreenAdapter = null
        super.onDestroyView()
    }

    private fun setUpRecyclerView() {
        binding.homeItemsRecyclerView.apply {
            adapter = homeScreenAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun registerCarouselClickEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeScreenAdapter?.carouselClickEvent?.collect {
                    when (it) {
                        is CarouselItem.CharacterItem -> {
                            viewModel.onAction(HomeAction.NavigateToCharacterDetailsScreen(it.id))
                        }
                        is CarouselItem.EpisodeItem -> {
                            viewModel.onAction(HomeAction.NavigateToEpisodeDetailsScreen(it.id))
                        }
                        else -> {}
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeScreenAdapter?.carouselPlayButtonClickEvent?.collect {
                    when (it) {
                        is CarouselItem.EpisodeItem -> {
                            if (it.id != castVideoService.castedVideoId) {
                                viewModel.onAction(HomeAction.NavigateToVideoPlayerScreen(it.id))
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun registerPlaylistClickEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeScreenAdapter?.playlistItemClickEvent?.collect {
                    when (it) {
                        is PlaylistItem.CharacterItem -> {
                            viewModel.onAction(HomeAction.NavigateToCharacterDetailsScreen(it.id))
                        }
                        is PlaylistItem.EpisodeItem -> {
                            viewModel.onAction(HomeAction.NavigateToEpisodeDetailsScreen(it.id))
                        }
                        else -> {}
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeScreenAdapter?.playlistPlayButtonClickEvent?.collect {
                    when (it) {
                        is PlaylistItem.EpisodeItem -> {
                            if (it.id != castVideoService.castedVideoId) {
                                viewModel.onAction(HomeAction.NavigateToVideoPlayerScreen(it.id))
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun registerSeeAllClickEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeScreenAdapter?.seeAllClickEvent?.collect {
                    viewModel.onAction(HomeAction.NavigateToSeeAllScreen(it))
                }
            }
        }
    }

    private fun navigateToSeeAllFragment(playlist: Playlist) {
        homeNavigator.navigateTo(Screen.SeeAll(playlist))
    }

    private fun navigateToCharacterDetails(id: String) {
        homeNavigator.navigateTo(Screen.CharacterDetails(id))
    }

    private fun navigateToEpisodeDetails(id: String) {
        homeNavigator.navigateTo(Screen.EpisodeDetails(id))
    }

    private fun navigateToVideoPlayerScreen(episodeId: String) {
        homeNavigator.navigateTo(Screen.VideoPlayer(episodeId))
    }

    private fun updateUI(state: HomeState.DataLoaded) {
        hideLoading()
        if (state.homeItems.isNotEmpty()) {
            homeScreenAdapter?.submitList(state.homeItems)
        }
    }

    private fun showLoading() {
        binding.homeProgressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.homeProgressBar.isVisible = false
    }

    private fun showError(state: HomeState.Error) {
        hideLoading()
        ErrorDialogFragment.show(childFragmentManager, state.exception)
    }
}