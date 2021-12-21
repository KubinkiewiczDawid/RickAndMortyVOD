package com.dawidk.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.common.utils.hideKeyboardOnAction
import com.dawidk.common.utils.isTablet
import com.dawidk.search.databinding.SearchFragmentBinding
import com.dawidk.search.model.SearchItem
import com.dawidk.search.navigation.Screen
import com.dawidk.search.navigation.SearchNavigator
import com.dawidk.search.state.SearchAction
import com.dawidk.search.state.SearchEvent
import com.dawidk.search.state.SearchState
import com.dawidk.search.ui.SearchAdapter
import com.dawidk.search.utils.textChanges
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.search_fragment), ErrorDialogFragment.Callback {

    private val viewModel by viewModel<SearchViewModel>()
    private val binding by viewBinding(SearchFragmentBinding::bind)
    private val networkMonitor: NetworkMonitor by inject()
    private val searchNavigator: SearchNavigator by inject()
    private var searchAdapter: SearchAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = SearchAdapter()
        searchNavigator.navController = findNavController()

        checkInternetConnection()
        setUpRecyclerView()

        binding.tfSearchField.editText?.hideKeyboardOnAction(
            requireContext(),
            EditorInfo.IME_ACTION_SEARCH
        )
        
        registerSearchFieldListener()
        registerStateListener()
        registerClickEventListener()
        registerEventListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchAdapter = null
    }

    private fun setUpRecyclerView() {
        binding.rvSearchResults.apply {
            adapter = searchAdapter
            layoutManager = GridLayoutManager(
                activity,
                if (context.isTablet()) {
                    resources.getInteger(R.integer.grid_layout_tablet_span_count)
                } else {
                    resources.getInteger(R.integer.grid_layout_span_count)
                }
            )
        }
    }

    private fun registerSearchFieldListener() {
        lifecycleScope.launch {
            binding.tfSearchField.editText?.textChanges()
                ?.debounce(300)
                ?.map {
                    SearchAction.Search(it.toString())
                }?.collect { searchAction ->
                    viewModel.onAction(searchAction)
                }
        }
    }

    private fun registerStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    when (it) {
                        is SearchState.Loading -> showLoading()
                        is SearchState.Error -> showError(it)
                        is SearchState.DataLoaded -> dataReceived(it)
                    }
                }
            }
        }
    }

    private fun dataReceived(state: SearchState.DataLoaded) {
        hideLoading()
        lifecycleScope.launch {
            searchAdapter?.submitList(state.data)
        }
    }

    private fun showLoading() {
        binding.searchProgressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.searchProgressBar.isVisible = false
    }

    private fun showError(state: SearchState.Error) {
        hideLoading()
        ErrorDialogFragment.show(childFragmentManager, state.exception)
    }

    override fun onPositiveButtonClicked(error: Throwable) {
        checkInternetConnection()
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

    private fun navigateToCharacterDetails(id: String) {
        searchNavigator.navigateTo(Screen.CharacterDetails(id))
    }

    private fun navigateToEpisodeDetails(id: String) {
        searchNavigator.navigateTo(Screen.EpisodeDetails(id))
    }

    private fun registerEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect {
                    when (it) {
                        is SearchEvent.NavigateToCharacterDetailsScreen -> navigateToCharacterDetails(
                            it.id
                        )
                        is SearchEvent.NavigateToEpisodeDetailsScreen -> navigateToEpisodeDetails(
                            it.id
                        )
                    }
                }
            }
        }
    }

    private fun registerClickEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchAdapter?.clickEvent?.collect {
                    when (it) {
                        is SearchItem.CharacterItem -> {
                            viewModel.onAction(SearchAction.NavigateToCharacterDetailsScreen(it.id))
                        }
                        is SearchItem.EpisodeItem -> {
                            viewModel.onAction(SearchAction.NavigateToEpisodeDetailsScreen(it.id))
                        }
                    }
                }
            }
        }
    }
}