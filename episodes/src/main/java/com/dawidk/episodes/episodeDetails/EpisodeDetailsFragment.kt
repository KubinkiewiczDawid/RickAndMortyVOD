package com.dawidk.episodes.episodeDetails

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.core.utils.DataLoadingException
import com.dawidk.episodes.R
import com.dawidk.episodes.databinding.EpisodeDetailsFragmentBinding
import com.dawidk.episodes.episodeDetails.navigation.EpisodeDetailsNavigator
import com.dawidk.episodes.episodeDetails.navigation.Screen
import com.dawidk.episodes.episodeDetails.state.EpisodeDetailsAction
import com.dawidk.episodes.episodeDetails.state.EpisodeDetailsEvent
import com.dawidk.episodes.episodeDetails.state.EpisodeDetailsState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val IMAGES_NUMBER = 4

class EpisodeDetailsFragment : Fragment(R.layout.episode_details_fragment),
    ErrorDialogFragment.Callback {

    private val viewModel by viewModel<EpisodeDetailsViewModel>()
    private val binding by viewBinding(EpisodeDetailsFragmentBinding::bind)
    private lateinit var episodeId: String
    private var episodeDetailsAdapter: EpisodeDetailsAdapter? = null
    private val networkMonitor: NetworkMonitor by inject()
    private val episodeDetailsNavigator: EpisodeDetailsNavigator by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        episodeDetailsNavigator.navController = findNavController()

        checkInternetConnection()
        val safeArgs: EpisodeDetailsFragmentArgs by navArgs()
        episodeId = safeArgs.id
        episodeDetailsAdapter = EpisodeDetailsAdapter(episodeId)

        viewModel.onAction(EpisodeDetailsAction.GetEpisodeById(episodeId))
        setUpRecyclerView()
        registerClickEventListener()
        registerEventListener()
        registerStateListener()
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

    private fun registerEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect {
                    when (it) {
                        is EpisodeDetailsEvent.NavigateToCharacterDetails -> navigateToCharacterDetails(
                            it.id
                        )
                        is EpisodeDetailsEvent.NavigateToVideoPlayerScreen -> navigateToVideoPlayerScreen(
                            it.id
                        )
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
                        is EpisodeDetailsState.GetEpisodeSuccess -> {
                            updateUI(it)
                        }
                        is EpisodeDetailsState.Loading -> showLoading()
                        is EpisodeDetailsState.Error -> showError(it)
                    }
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

    override fun onPositiveButtonClicked(error: Throwable) {
        if (error is DataLoadingException) {
            viewModel.onAction(EpisodeDetailsAction.GetEpisodeById(episodeId))
        } else {
            checkInternetConnection()
        }
    }

    override fun onNegativeButtonClicked() {
        requireActivity().finish()
    }

    private fun checkInternetConnection() {
        viewLifecycleOwner.lifecycleScope.launch {
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