package com.dawidk.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.mvi.BaseFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.common.utils.collectFromState
import com.dawidk.common.utils.hideKeyboardOnAction
import com.dawidk.common.utils.isTablet
import com.dawidk.search.databinding.SearchFragmentBinding
import com.dawidk.search.model.SearchItem
import com.dawidk.search.mvi.SearchAction
import com.dawidk.search.mvi.SearchEvent
import com.dawidk.search.mvi.SearchState
import com.dawidk.search.navigation.Screen
import com.dawidk.search.navigation.SearchNavigator
import com.dawidk.search.ui.SearchAdapter
import com.dawidk.search.utils.textChanges
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment :
    BaseFragment<SearchEvent, SearchAction, SearchState, SearchViewModel, SearchFragmentBinding>(R.layout.search_fragment) {

    override val viewModel by viewModel<SearchViewModel>()
    override val binding by viewBinding(SearchFragmentBinding::bind)
    override val networkMonitor: NetworkMonitor by inject()
    private val searchNavigator: SearchNavigator by inject()
    private var searchAdapter: SearchAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = SearchAdapter()
        searchNavigator.navController = findNavController()

        setUpRecyclerView()

        binding.tfSearchField.editText?.hideKeyboardOnAction(
            requireContext(),
            EditorInfo.IME_ACTION_SEARCH
        )

        registerSearchFieldListener()
        registerClickEventListener()
    }

    override fun handleEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.NavigateToCharacterDetailsScreen -> navigateToCharacterDetails(
                event.id
            )
            is SearchEvent.NavigateToEpisodeDetailsScreen -> navigateToEpisodeDetails(
                event.id
            )
        }
    }

    override fun handleState(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Error -> showError(state)
            is SearchState.DataLoaded -> dataReceived(state)
        }
    }

    override fun onDataLoadingException() {
        SearchState.DataLoaded(emptyList())
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

    private fun navigateToCharacterDetails(id: String) {
        searchNavigator.navigateTo(Screen.CharacterDetails(id))
    }

    private fun navigateToEpisodeDetails(id: String) {
        searchNavigator.navigateTo(Screen.EpisodeDetails(id))
    }

    private fun registerClickEventListener() {
        viewLifecycleOwner.collectFromState(Lifecycle.State.STARTED, searchAdapter?.clickEvent) {
            when (it) {
                is SearchItem.CharacterItem -> {
                    viewModel.onAction(SearchAction.NavigateToCharacterDetailsScreen(it.id))
                }
                is SearchItem.EpisodeItem -> {
                    viewModel.onAction(SearchAction.NavigateToEpisodeDetailsScreen(it.id))
                }
                is SearchItem.LocationItem -> {}
            }
        }
    }
}