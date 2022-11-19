package com.dawidk.settings

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.registration.AuthEvent
import com.dawidk.common.registration.FirebaseAuthApi
import com.dawidk.common.registration.GoogleClientApi
import com.dawidk.common.registration.SignState
import com.dawidk.settings.databinding.SettingsFragmentBinding
import com.dawidk.settings.navigation.Screen
import com.dawidk.settings.navigation.SettingsNavigator
import com.dawidk.settings.state.SettingsAction
import com.dawidk.settings.state.SettingsEvent
import com.dawidk.settings.state.SettingsState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.settings_fragment), ErrorDialogFragment.Callback {

    private val viewModel by viewModel<SettingsViewModel>()
    private val binding by viewBinding(SettingsFragmentBinding::bind)
    private val settingsNavigator: SettingsNavigator by inject()
    private val googleClientApi: GoogleClientApi by inject()
    private val firebaseAuthClient: FirebaseAuthApi by inject()
    private var settingsAdapter: SettingsAdapter? = null
    private lateinit var settingsListProvider: SettingsListProvider

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
            divider.setDrawable(requireContext().getDrawable(R.drawable.line_divider)!!)
            addItemDecoration(divider)
        }

        registerEventListener()
        registerStateListener()
        registerSettingsProviderListener()
        registerGoogleClientListener()
        registerFirebaseAuthListener()

        if (savedInstanceState == null)
            viewModel.onAction(SettingsAction.Init)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        settingsAdapter = null
    }

    private fun registerEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect {
                    when (it) {
                        is SettingsEvent.NavigateToSignInScreen -> navigateToSignInFragment()
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
                        is SettingsState.DataLoaded -> updateUI(it)
                        is SettingsState.Error -> showError(it)
                        is SettingsState.Loading -> showLoading()
                    }
                }
            }
        }
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

    override fun onPositiveButtonClicked(error: Throwable) {}

    override fun onNegativeButtonClicked() {
        requireActivity().finish()
    }
}