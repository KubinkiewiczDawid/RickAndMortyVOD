package com.dawidk.location

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.mvi.BaseFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.common.utils.selectLayoutManager
import com.dawidk.location.databinding.FragmentLocationsBinding
import com.dawidk.location.state.LocationsAction
import com.dawidk.location.state.LocationsEvent
import com.dawidk.location.state.LocationsState
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationsFragment :
    BaseFragment<LocationsEvent, LocationsAction, LocationsState, LocationsViewModel, FragmentLocationsBinding>(
        R.layout.fragment_locations
    ) {

    override val viewModel by viewModel<LocationsViewModel>()
    override val binding by viewBinding(FragmentLocationsBinding::bind)
    override val networkMonitor: NetworkMonitor by inject()
    private var locationsAdapter: LocationsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            viewModel.onAction(LocationsAction.Init)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationsAdapter = LocationsAdapter()

        setUpRecyclerView()

        registerUiListener()
    }

    override fun handleEvent(event: LocationsEvent) {}

    override fun handleState(state: LocationsState) {
        when (state) {
            is LocationsState.Loading -> showLoading()
            is LocationsState.Error -> showError(state)
            is LocationsState.DataLoaded -> dataReceived(state)
        }
    }

    override fun onDataLoadingException() {
        viewModel.onAction(LocationsAction.Init)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationsAdapter = null
    }

    private fun setUpRecyclerView() {
        binding.locationsList.apply {
            layoutManager = selectLayoutManager(context)
            adapter = locationsAdapter
        }
    }

    private fun registerUiListener() {
        lifecycleScope.launch {
            locationsAdapter?.addLoadStateListener { loadStates ->
                loadStates.refresh.let {
                    when (it) {
                        is LoadState.Loading -> {
                            viewModel.onAction(LocationsAction.Load)
                        }
                        is LoadState.NotLoading -> {
                            viewModel.onAction(LocationsAction.DataLoaded)
                        }
                        is LoadState.Error -> {
                            viewModel.onAction(LocationsAction.HandleError(it.error))
                        }
                    }
                }
            }
        }
    }

    private fun dataReceived(state: LocationsState.DataLoaded) {
        hideLoading()
        lifecycleScope.launch {
            locationsAdapter?.submitData(state.data)
        }
    }

    private fun showLoading() {
        binding.loadingBar.isVisible = true
    }

    private fun hideLoading() {
        binding.loadingBar.isVisible = false
    }

    private fun showError(state: LocationsState.Error) {
        hideLoading()
        ErrorDialogFragment.show(childFragmentManager, state.exception)
    }
}