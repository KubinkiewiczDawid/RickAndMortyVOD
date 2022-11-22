package com.dawidk.episodes

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.mvi.BaseFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.common.utils.selectLayoutManager
import com.dawidk.core.domain.model.EpisodeFilter
import com.dawidk.episodes.databinding.EpisodesFragmentBinding
import com.dawidk.episodes.navigation.EpisodesNavigator
import com.dawidk.episodes.navigation.Screen
import com.dawidk.episodes.seasonpicker.SEASONS_LIST
import com.dawidk.episodes.seasonpicker.SELECTED_SEASON
import com.dawidk.episodes.seasonpicker.SeasonPickerFragment
import com.dawidk.episodes.seasonpicker.UPDATE_FUNCTION
import com.dawidk.episodes.mvi.EpisodeAction
import com.dawidk.episodes.mvi.EpisodeEvent
import com.dawidk.episodes.mvi.EpisodeState
import com.dawidk.episodes.utils.mapToPrettySeasonName
import com.dawidk.videoplayer.cast.service.CastVideoService
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable

class EpisodesFragment :
    BaseFragment<EpisodeEvent, EpisodeAction, EpisodeState, EpisodesViewModel, EpisodesFragmentBinding>(
        R.layout.episodes_fragment
    ) {

    override val viewModel by viewModel<EpisodesViewModel>()
    override val binding by viewBinding(EpisodesFragmentBinding::bind)
    override val networkMonitor: NetworkMonitor by inject()
    private val episodesNavigator: EpisodesNavigator by inject()
    private var episodesAdapter: EpisodesAdapter? = null
    private val castVideoService: CastVideoService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            viewModel.onAction(EpisodeAction.Init)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        episodesAdapter = EpisodesAdapter()
        episodesNavigator.navController = findNavController()

        setUpRecyclerView()
        setUpSeasonPicker()

        registerClickEventListener()
    }

    override fun handleEvent(event: EpisodeEvent) {
        when (event) {
            is EpisodeEvent.NavigateToEpisodeDetails -> navigateToEpisodeDetails(
                event.id
            )
            is EpisodeEvent.NavigateToSeasonsList -> navigateToSeasonsList(
                event.selectedSeason,
                event.seasonsList
            )
            is EpisodeEvent.NavigateToVideoPlayerScreen -> navigateToVideoPlayerScreen(
                event.episodeId
            )
        }
    }

    override fun handleState(state: EpisodeState) {
        when (state) {
            is EpisodeState.Loading -> showLoading()
            is EpisodeState.Error -> showError(state)
            is EpisodeState.DataLoaded -> dataReceived(state)
        }
    }

    override fun onDataLoadingException() {
        viewModel.onAction(EpisodeAction.Init)
    }

    override fun onDestroyView() {
        episodesAdapter = null
        super.onDestroyView()
    }

    private fun setUpRecyclerView() {
        binding.episodesHomeRecyclerView.apply {
            adapter = episodesAdapter
            layoutManager = selectLayoutManager(context)
            addItemDecoration(DividerItemDecoration(activity, RecyclerView.VERTICAL))
        }
    }

    private fun registerClickEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                episodesAdapter?.clickEvent?.collect {
                    viewModel.onAction(EpisodeAction.NavigateToEpisodeDetailsScreen(it.id))
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                episodesAdapter?.playButtonClickEvent?.collect {
                    if (it.id != castVideoService.castedVideoId) {
                        viewModel.onAction(EpisodeAction.NavigateToVideoPlayerScreen(it.id))
                    }
                }
            }
        }
    }

    private fun setUpSeasonPicker() {
        binding.btnSeasonPicker.setOnClickListener {
            viewModel.onAction(EpisodeAction.NavigateToSeasonsList)
        }
    }

    private fun updateSelectedSeason(seasonId: String) {
        viewModel.onAction(EpisodeAction.Update(EpisodeFilter(episode = seasonId)))
    }

    private fun navigateToSeasonsList(
        selectedSeason: String,
        seasonsList: List<String>,
    ) {
        val updateSeason: (seasonId: String) -> Unit = {
            updateSelectedSeason(it)
        }
        val bundle = bundleOf(
            SELECTED_SEASON to selectedSeason,
            SEASONS_LIST to seasonsList,
            UPDATE_FUNCTION to updateSeason as Serializable
        )
        childFragmentManager.commit {
            setReorderingAllowed(true)
            add<SeasonPickerFragment>(R.id.seasons_fragment_container, args = bundle)
            addToBackStack(null)
        }
    }

    private fun navigateToEpisodeDetails(id: String) {
        episodesNavigator.navigateTo(Screen.EpisodeDetails(id))
    }

    private fun navigateToVideoPlayerScreen(episodeId: String) {
        episodesNavigator.navigateTo(Screen.VideoPlayer(episodeId))
    }

    private fun dataReceived(state: EpisodeState.DataLoaded) {
        hideLoading()
        lifecycleScope.launch {
            binding.tvNumberOfEpisodes.text =
                "${state.data.episodesMap.getValue(state.data.chosenSeason).size} " + requireContext().resources.getString(
                    R.string.episodes
                )
            binding.btnSeasonPicker.text =
                state.data.chosenSeason.mapToPrettySeasonName(requireContext())
            episodesAdapter?.submitList(state.data.episodesMap.getValue(state.data.chosenSeason))
        }
    }

    private fun showLoading() {
        hideSeasonsDetails()
        binding.episodesHomeProgressBar.isVisible = true
    }

    private fun hideLoading() {
        showSeasonsDetails()
        binding.episodesHomeProgressBar.isVisible = false
    }

    private fun showSeasonsDetails() {
        binding.rlSeasonsDetails.isVisible = true
    }

    private fun hideSeasonsDetails() {
        binding.rlSeasonsDetails.isVisible = false
    }

    private fun showError(state: EpisodeState.Error) {
        hideLoading()
        ErrorDialogFragment.show(childFragmentManager, state.exception)
    }
}