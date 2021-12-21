package com.dawidk.characters.charactersHome

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.dawidk.characters.R
import com.dawidk.characters.databinding.CharacterHomeFragmentBinding
import com.dawidk.characters.navigation.CharactersNavigator
import com.dawidk.characters.navigation.Screen
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.common.utils.selectLayoutManager
import com.dawidk.core.utils.DataLoadingException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CharacterHomeFragment : Fragment(R.layout.character_home_fragment),
    ErrorDialogFragment.Callback {

    private val binding by viewBinding(CharacterHomeFragmentBinding::bind)
    private val characterHomeAdapter = CharacterHomeAdapter()
    private val viewModel by viewModel<CharacterHomeViewModel>()
    private val charactersNavigator: CharactersNavigator by inject()
    private val networkMonitor: NetworkMonitor by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            viewModel.onAction(CharacterAction.Init)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        charactersNavigator.navController = findNavController()

        checkInternetConnection()

        setUpRecyclerView()

        registerClickEventListener()
        registerEventListener()
        registerStateListener()
        registerUiListener()
    }

    private fun setUpRecyclerView() {
        binding.characterHomeRecyclerView.apply {
            adapter = characterHomeAdapter
            layoutManager = selectLayoutManager(context)
        }
    }

    private fun registerClickEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                characterHomeAdapter.clickEvent.collect {
                    viewModel.onAction(CharacterAction.NavigateToDetailsScreen(it.id))
                }
            }
        }
    }

    private fun registerEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect {
                    when (it) {
                        is CharacterEvent.NavigateToCharacterDetails -> navigateToCharacterDetails(
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
                        is CharacterState.Loading -> showLoading()
                        is CharacterState.Error -> showError(it)
                        is CharacterState.DataLoaded -> dataReceived(it)
                    }
                }
            }
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

    override fun onPositiveButtonClicked(error: Throwable) {
        if (error is DataLoadingException) {
            viewModel.onAction(CharacterAction.Init)
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