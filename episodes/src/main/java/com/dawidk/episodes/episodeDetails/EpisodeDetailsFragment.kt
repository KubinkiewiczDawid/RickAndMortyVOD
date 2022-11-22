package com.dawidk.episodes.episodeDetails

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.mvi.BaseFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.episodes.R
import com.dawidk.episodes.databinding.EpisodeDetailsFragmentBinding
import com.dawidk.episodes.episodeDetails.navigation.EpisodeDetailsNavigator
import com.dawidk.episodes.episodeDetails.navigation.Screen
import com.dawidk.episodes.episodeDetails.mvi.EpisodeDetailsAction
import com.dawidk.episodes.episodeDetails.mvi.EpisodeDetailsEvent
import com.dawidk.episodes.episodeDetails.mvi.EpisodeDetailsState
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EpisodeDetailsFragment :
    BaseFragment<EpisodeDetailsEvent, EpisodeDetailsAction, EpisodeDetailsState, EpisodeDetailsViewModel, EpisodeDetailsFragmentBinding>(
        R.layout.episode_details_fragment
    ) {

    override val viewModel by viewModel<EpisodeDetailsViewModel>()
    override val binding by viewBinding(EpisodeDetailsFragmentBinding::bind)
    override val networkMonitor: NetworkMonitor by inject()
    private lateinit var episodeId: String
    private var episodeDetailsAdapter: EpisodeDetailsAdapter? = null
    private val episodeDetailsNavigator: EpisodeDetailsNavigator by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        episodeDetailsNavigator.navController = findNavController()

        val safeArgs: EpisodeDetailsFragmentArgs by navArgs()
        episodeId = safeArgs.id
        episodeDetailsAdapter = EpisodeDetailsAdapter(episodeId)

        viewModel.onAction(EpisodeDetailsAction.GetEpisodeById(episodeId))
        setUpRecyclerView()
        registerClickEventListener()
    }

    override fun handleEvent(event: EpisodeDetailsEvent) {
        when (event) {
            is EpisodeDetailsEvent.NavigateToCharacterDetails -> navigateToCharacterDetails(
                event.id
            )
            is EpisodeDetailsEvent.NavigateToVideoPlayerScreen -> navigateToVideoPlayerScreen(
                event.id
            )
        }
    }

    override fun handleState(state: EpisodeDetailsState) {
        when (state) {
            is EpisodeDetailsState.GetEpisodeSuccess -> {
                updateUI(state)
            }
            is EpisodeDetailsState.Loading -> showLoading()
            is EpisodeDetailsState.Error -> showError(state)
        }
    }

    override fun onDataLoadingException() {
        viewModel.onAction(EpisodeDetailsAction.GetEpisodeById(episodeId))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        episodeDetailsAdapter = null
    }

    private fun setUpRecyclerView() {
        binding.detailsRv.apply {
            adapter = episodeDetailsAdapter
            layoutManager = LinearLayoutManager(
                activity
            )
        }
    }

    private fun registerClickEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                episodeDetailsAdapter?.clickEvent?.collect {
                    viewModel.onAction(EpisodeDetailsAction.NavigateToCharacterDetailsScreen(it.id))
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                episodeDetailsAdapter?.episodePlayClickEvent?.collect {
                    viewModel.onAction(EpisodeDetailsAction.NavigateToVideoPlayerScreen(episodeId))
                }
            }
        }
    }

    private fun navigateToCharacterDetails(id: String) {
        episodeDetailsNavigator.navigateTo(Screen.CharacterDetails(id))
    }

    private fun navigateToVideoPlayerScreen(id: String) {
        episodeDetailsNavigator.navigateTo(Screen.VideoPlayer(id))
    }

    private fun updateUI(state: EpisodeDetailsState.GetEpisodeSuccess) {
        hideLoading()
        episodeDetailsAdapter?.submitList(state.episodeItems)
    }

    private fun showLoading() {
        binding.episodeDetailsProgressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.episodeDetailsProgressBar.isVisible = false
    }

    private fun showError(state: EpisodeDetailsState.Error) {
        hideLoading()
        ErrorDialogFragment.show(childFragmentManager, state.exception)
    }
}