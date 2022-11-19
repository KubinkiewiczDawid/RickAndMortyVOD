package com.dawidk.location.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.common.utils.selectLayoutManager
import com.dawidk.core.utils.DataLoadingException
import com.dawidk.location.R
import com.dawidk.location.databinding.FragmentLocationsBinding
import com.dawidk.location.state.LocationsAction
import com.dawidk.location.state.LocationsState
import com.dawidk.location.ui.adapters.LocationsAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationsFragment : Fragment(R.layout.fragment_locations), ErrorDialogFragment.Callback {

    private val binding by viewBinding(FragmentLocationsBinding::bind)
    private val viewModel by viewModel<LocationsViewModel>()
    private val networkMonitor: NetworkMonitor by inject()
    private var locationsAdapter: LocationsAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationsAdapter = LocationsAdapter()
        checkInternetConnection()

        setUpRecyclerView()

        registerStateListener()
        registerUiListener()

        if (savedInstanceState == null)
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

    private fun registerStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    when (it) {
                        is LocationsState.Loading -> showLoading()
                        is LocationsState.Error -> showError(it)
                        is LocationsState.DataLoaded -> dataReceived(it)
                    }
                }
            }
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

    override fun onPositiveButtonClicked(error: Throwable) {
        if (error is DataLoadingException) {
            viewModel.onAction(LocationsAction.Init)
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