package com.dawidk.common.mvi

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import com.dawidk.common.R
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.common.utils.collectFromState
import com.dawidk.core.utils.DataLoadingException

abstract class BaseFragment<EVENT : ViewEvent, ACTION : ViewAction, STATE : ViewState,
        VM : BaseViewModel<EVENT, ACTION, STATE>, VB: ViewBinding>(@LayoutRes contentLayoutId: Int): Fragment(contentLayoutId), ErrorDialogFragment.Callback {

    protected abstract val viewModel: VM
    protected abstract val binding: VB
    protected abstract val networkMonitor: NetworkMonitor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInternetConnection()
        registerEventListener()
        registerStateListener()
    }

    private fun registerEventListener() {
        viewLifecycleOwner.collectFromState(Lifecycle.State.STARTED, viewModel.event) {
            handleEvent(it)
        }
    }

    protected abstract fun handleEvent(event: EVENT)

    private fun registerStateListener() {
        viewLifecycleOwner.collectFromState(Lifecycle.State.STARTED, viewModel.state) {
            handleState(it)
        }
    }

    protected abstract fun handleState(state: STATE)

    override fun onPositiveButtonClicked(error: Throwable) {
        if (error is DataLoadingException) {
            onDataLoadingException()
        } else {
            checkInternetConnection()
        }
    }

    protected abstract fun onDataLoadingException()

    override fun onNegativeButtonClicked() {
        requireActivity().finish()
    }

    private fun checkInternetConnection() {
        viewLifecycleOwner.collectFromState(Lifecycle.State.STARTED, networkMonitor.state) {
            when (it) {
                false -> {
                    ErrorDialogFragment.show(
                        childFragmentManager,
                        Throwable(getString(R.string.no_internet_error_message))
                    )
                }
                else -> {}
            }
        }
    }
}