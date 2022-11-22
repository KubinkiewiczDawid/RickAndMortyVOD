package com.dawidk.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.mvi.BaseFragment
import com.dawidk.common.registration.AuthEvent
import com.dawidk.common.registration.FirebaseAuthApi
import com.dawidk.common.registration.GoogleClientApi
import com.dawidk.common.registration.SignState
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.settings.databinding.SettingsFragmentBinding
import com.dawidk.settings.navigation.Screen
import com.dawidk.settings.navigation.SettingsNavigator
import com.dawidk.settings.mvi.SettingsAction
import com.dawidk.settings.mvi.SettingsEvent
import com.dawidk.settings.mvi.SettingsState
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment :
    BaseFragment<SettingsEvent, SettingsAction, SettingsState, SettingsViewModel, SettingsFragmentBinding>(
        R.layout.settings_fragment
    ) {

    override val viewModel by viewModel<SettingsViewModel>()
    override val binding by viewBinding(SettingsFragmentBinding::bind)
    override val networkMonitor: NetworkMonitor by inject()
    private val settingsNavigator: SettingsNavigator by inject()
    private val googleClientApi: GoogleClientApi by inject()
    private val firebaseAuthClient: FirebaseAuthApi by inject()
    private var settingsAdapter: SettingsAdapter? = null
    private lateinit var settingsListProvider: SettingsListProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            viewModel.onAction(SettingsAction.Init)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsAdapter = SettingsAdapter()
        settingsNavigator.navController = findNavController()

        settingsListProvider = SettingsListProvider(
            requireContext(),
            requireActivity(),
            googleClientApi,
            firebaseAuthClient
        )

        binding.rvSettings.apply {
            adapter = settingsAdapter
            layoutManager = LinearLayoutManager(activity)
            val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            AppCompatResources.getDrawable(requireContext(), R.drawable.line_divider)
                ?.let { divider.setDrawable(it) }
            addItemDecoration(divider)
        }

        registerSettingsProviderListener()
        registerGoogleClientListener()
        registerFirebaseAuthListener()
    }

    override fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.NavigateToSignInScreen -> navigateToSignInFragment()
        }
    }

    override fun handleState(state: SettingsState) {
        when (state) {
            is SettingsState.DataLoaded -> updateUI(state)
            is SettingsState.Error -> showError(state)
            is SettingsState.Loading -> showLoading()
        }
    }

    override fun onDataLoadingException() {
        viewModel.onAction(SettingsAction.Init)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        settingsAdapter = null
    }

    private fun registerSettingsProviderListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsListProvider.settingsFlow.collect {
                    settingsAdapter?.submitList(it)
                }
            }
        }
    }

    private fun registerGoogleClientListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                googleClientApi.event.collect {
                    if (it is SignState.SignedOut) {
                        viewModel.onAction(SettingsAction.NavigateToSignInScreen)
                    }
                }
            }
        }
    }

    private fun registerFirebaseAuthListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                firebaseAuthClient.event.collect {
                    if (it is AuthEvent.SignedOut) {
                        viewModel.onAction(SettingsAction.NavigateToSignInScreen)
                    }
                }
            }
        }
    }

    private fun navigateToSignInFragment() {
        settingsNavigator.navigateTo(Screen.SignInScreen)
        activity?.finish()
    }

    private fun updateUI(state: SettingsState.DataLoaded) {
        hideLoading()
        settingsListProvider.addUserInfo(state.data)
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.progressBar.isVisible = false
    }

    private fun showError(state: SettingsState.Error) {
        hideLoading()
        ErrorDialogFragment.show(childFragmentManager, state.exception)
    }
}