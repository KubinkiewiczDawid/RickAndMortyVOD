package com.dawidk.characters.characterDetails

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
import com.dawidk.characters.R
import com.dawidk.characters.characterDetails.navigation.CharacterDetailsNavigator
import com.dawidk.characters.characterDetails.navigation.Screen
import com.dawidk.characters.characterDetails.mvi.CharacterDetailsAction
import com.dawidk.characters.characterDetails.mvi.CharacterDetailsEvent
import com.dawidk.characters.characterDetails.mvi.CharacterDetailsState
import com.dawidk.characters.databinding.CharacterDetailsFragmentBinding
import com.dawidk.characters.model.CharacterItem
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.mvi.BaseFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.core.utils.DataLoadingException
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CharacterDetailsFragment :
    BaseFragment<CharacterDetailsEvent, CharacterDetailsAction, CharacterDetailsState, CharacterDetailsViewModel, CharacterDetailsFragmentBinding>(
        R.layout.character_details_fragment
    ) {

    override val viewModel by viewModel<CharacterDetailsViewModel>()
    override val binding by viewBinding(CharacterDetailsFragmentBinding::bind)
    override val networkMonitor: NetworkMonitor by inject()
    private lateinit var characterId: String
    private var characterDetailsAdapter: CharacterDetailsAdapter? = null
    private var characterItemsList: List<CharacterItem> = emptyList()
    private val characterDetailsNavigator: CharacterDetailsNavigator by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val safeArgs: CharacterDetailsFragmentArgs by navArgs()
        characterId = safeArgs.id

        characterDetailsAdapter = CharacterDetailsAdapter()

        characterDetailsNavigator.navController = findNavController()

        binding.detailsRv.apply {
            adapter = characterDetailsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.onAction(CharacterDetailsAction.GetCharacterById(characterId))

        registerClickEventListeners()
    }

    override fun handleEvent(event: CharacterDetailsEvent) {
        when (event) {
            is CharacterDetailsEvent.NavigateToEpisodeDetails -> {
                navigateToEpisodeDetails(event.episodeId)
            }
        }
    }

    override fun handleState(state: CharacterDetailsState) {
        when (state) {
            is CharacterDetailsState.Loading -> showLoading()
            is CharacterDetailsState.GetCharacterSuccess -> {
                updateUI(state)
            }
            is CharacterDetailsState.GetEpisodes -> {
                updateTab(state)
            }
            is CharacterDetailsState.GetLocations -> {
                updateTab(state)
            }
            is CharacterDetailsState.Error -> showError(state)
        }
    }

    override fun onDataLoadingException() {
        viewModel.onAction(CharacterDetailsAction.GetCharacterById(characterId))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        characterDetailsAdapter = null
    }

    private fun registerClickEventListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                characterDetailsAdapter?.onEpisodeClickEvent?.collect {
                    viewModel.onAction(CharacterDetailsAction.NavigateToEpisodeDetailsScreen(it.id))
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                characterDetailsAdapter?.onTabClickEvent?.collect {
                    when (it) {
                        resources.getString(R.string.episodes) -> {
                            viewModel.onAction(CharacterDetailsAction.GetEpisodes)
                        }
                        resources.getString(R.string.locations) -> {
                            viewModel.onAction(CharacterDetailsAction.GetLocations)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                characterDetailsAdapter?.selectedTabEvent?.collect {
                    when (it) {
                        resources.getString(R.string.episodes) -> {
                            viewModel.onAction(CharacterDetailsAction.GetEpisodes)
                        }
                        resources.getString(R.string.locations) -> {
                            viewModel.onAction(CharacterDetailsAction.GetLocations)
                        }
                    }
                }
            }
        }
    }

    private fun navigateToEpisodeDetails(id: String) {
        characterDetailsNavigator.navigateTo(Screen.EpisodeDetails(id))
    }


    private fun updateUI(state: CharacterDetailsState.GetCharacterSuccess) {
        hideLoading()
        characterItemsList = state.characterItems
        characterDetailsAdapter?.submitList(
            characterItemsList
        )
    }

    private fun updateTab(characterDetailsState: CharacterDetailsState) {
        when (characterDetailsState) {
            is CharacterDetailsState.GetEpisodes -> {
                characterDetailsAdapter?.submitList(characterItemsList.filter {
                    it !is CharacterItem.CharacterLocationItem
                })
            }
            else -> {
                characterDetailsAdapter?.submitList(characterItemsList.filter {
                    it !is CharacterItem.CharacterEpisodesItem
                })
            }
        }
    }

    private fun showLoading() {
        binding.detailsProgressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.detailsProgressBar.isVisible = false
    }

    private fun showError(state: CharacterDetailsState.Error) {
        hideLoading()
        ErrorDialogFragment.show(childFragmentManager, state.exception)
    }
}
