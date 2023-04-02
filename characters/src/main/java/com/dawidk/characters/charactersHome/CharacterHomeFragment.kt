package com.dawidk.characters.charactersHome

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.dawidk.characters.R
import com.dawidk.characters.charactersHome.mvi.CharacterAction
import com.dawidk.characters.charactersHome.mvi.CharacterEvent
import com.dawidk.characters.charactersHome.mvi.CharacterState
import com.dawidk.characters.charactersHome.navigation.CharactersNavigator
import com.dawidk.characters.charactersHome.navigation.Screen
import com.dawidk.characters.databinding.CharacterHomeFragmentBinding
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.mvi.BaseFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.common.utils.collectFromState
import com.dawidk.common.utils.selectLayoutManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CharacterHomeFragment :
    BaseFragment<CharacterEvent, CharacterAction, CharacterState, CharacterHomeViewModel, CharacterHomeFragmentBinding>(
        R.layout.character_home_fragment
    ) {

    override val binding by viewBinding(CharacterHomeFragmentBinding::bind)
    override val viewModel by viewModel<CharacterHomeViewModel>()
    override val networkMonitor: NetworkMonitor by inject()
    private val characterHomeAdapter = CharacterHomeAdapter()
    private val charactersNavigator: CharactersNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            viewModel.onAction(CharacterAction.Init)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        charactersNavigator.navController = findNavController()

        setUpRecyclerView()
        registerClickEventListener()
        registerUiListener()
    }

    override fun handleEvent(event: CharacterEvent) {
        when (event) {
            is CharacterEvent.NavigateToCharacterDetails -> navigateToCharacterDetails(
                event.id
            )
        }
    }

    override fun handleState(state: CharacterState) {
        when (state) {
            is CharacterState.Loading -> showLoading()
            is CharacterState.Error -> showError(state)
            is CharacterState.DataLoaded -> dataReceived(state)
        }
    }

    override fun onDataLoadingException() {
        viewModel.onAction(CharacterAction.Init)
    }

    private fun setUpRecyclerView() {
        binding.characterHomeRecyclerView.apply {
            adapter = characterHomeAdapter
            layoutManager = selectLayoutManager(context)
        }
    }

    private fun registerClickEventListener() {
        viewLifecycleOwner.collectFromState(Lifecycle.State.STARTED, characterHomeAdapter.clickEvent) {
            viewModel.onAction(CharacterAction.NavigateToDetailsScreen(it.id))
        }
    }

    private fun registerUiListener() {
        lifecycleScope.launch {
            characterHomeAdapter.addLoadStateListener { loadStates ->
                loadStates.refresh.let {
                    when (it) {
                        is LoadState.Loading -> {
                            viewModel.onAction(CharacterAction.Load)
                        }
                        is LoadState.NotLoading -> {
                            viewModel.onAction(CharacterAction.DataLoaded)
                        }
                        is LoadState.Error -> {
                            viewModel.onAction(CharacterAction.HandleError(it.error))
                        }
                    }
                }
            }
        }
    }

    private fun navigateToCharacterDetails(id: String) {
        charactersNavigator.navigateTo(Screen.CharacterDetails(id))
    }

    private fun dataReceived(state: CharacterState.DataLoaded) {
        hideLoading()
        lifecycleScope.launch {
            characterHomeAdapter.submitData(state.data)
        }
    }

    private fun showLoading() {
        binding.mainProgressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.mainProgressBar.isVisible = false
    }

    private fun showError(state: CharacterState.Error) {
        hideLoading()
        ErrorDialogFragment.show(childFragmentManager, state.exception)
    }
}