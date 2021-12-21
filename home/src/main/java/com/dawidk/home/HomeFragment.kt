package com.dawidk.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.core.utils.DataLoadingException
import com.dawidk.home.databinding.HomeFragmentBinding
import com.dawidk.home.model.CarouselItem
import com.dawidk.home.model.Playlist
import com.dawidk.home.model.PlaylistItem
import com.dawidk.home.navigation.HomeNavigator
import com.dawidk.home.navigation.Screen
import com.dawidk.home.state.HomeAction
import com.dawidk.home.state.HomeEvent
import com.dawidk.home.state.HomeState
import com.dawidk.videoplayer.cast.service.CastVideoService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.home_fragment), ErrorDialogFragment.Callback {

    private val viewModel by viewModel<HomeViewModel>()
    private val binding by viewBinding(HomeFragmentBinding::bind)
    private val homeNavigator: HomeNavigator by inject()
    private val networkMonitor: NetworkMonitor by inject()
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

        checkInternetConnection()
        setUpRecyclerView()
        registerCarouselClickEvent()
        registerPlaylistClickEventListener()
        registerSeeAllClickEventListener()
        registerEventListener()
        registerStateListener()
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

    private fun registerEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect {
                    when (it) {
                        is HomeEvent.NavigateToSeeAllScreen -> navigateToSeeAllFragment(
                            it.playlist
                        )
                        is HomeEvent.NavigateToCharacterDetailsScreen -> navigateToCharacterDetails(
                            it.id
                        )
                        is HomeEvent.NavigateToEpisodeDetailsScreen -> navigateToEpisodeDetails(
                            it.id
                        )
                        is HomeEvent.NavigateToVideoPlayerScreen -> navigateToVideoPlayerScreen(it.episodeId)
                    }
                }
            }
        }
    }

    private fun registerStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    when (it) {
                        is HomeState.DataLoaded -> updateUI(it)
                        is HomeState.Error -> showError(it)
                        is HomeState.Loading -> showLoading()
                    }
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

    override fun onPositiveButtonClicked(error: Throwable) {
        if (error is DataLoadingException) {
            viewModel.onAction(HomeAction.LoadHomeItems)
        } else {
            checkInternetConnection()
        }
    }

    override fun onNegativeButtonClicked() {
        requireActivity().finish()
    }

    private fun checkInternetConnection() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkMonitor.state.collect {
                    when (it) {
                        false -> {
                            ErrorDialogFragment.show(
                                childFragmentManager,
                                Throwable(getString(R.string.no_internet_error_message))
                            )
                        }
                    }
                }
            }
        }
    }
}