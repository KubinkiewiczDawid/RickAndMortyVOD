package com.dawidk.registration

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.registration.AuthEvent
import com.dawidk.common.registration.FirebaseAuthApi
import com.dawidk.common.utils.fetchClick
import com.dawidk.registration.R.string
import com.dawidk.registration.databinding.SignUpFragmentBinding
import com.dawidk.registration.navigation.RegistrationNavigator
import com.dawidk.registration.navigation.Screen
import com.dawidk.registration.state.SignUpAction
import com.dawidk.registration.state.SignUpEvent
import com.dawidk.registration.state.SignUpState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpFragment : Fragment(R.layout.sign_up_fragment) {

    private val binding by viewBinding(SignUpFragmentBinding::bind)
    private val viewModel by viewModel<SignUpViewModel>()
    private val firebaseAuthClient: FirebaseAuthApi by inject()
    private val registrationNavigator: RegistrationNavigator by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registrationNavigator.navController = findNavController()

        registerStateListener()
        registerEventListener()
        registerClickEventListener()
        registerFirebaseAuthClientListener()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onAction(SignUpAction.ClearErrorMessages)
    }

    private fun registerStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    when (it) {
                        is SignUpState.InvalidSignUpData -> updateUI(
                            it
                        )
                    }
                }
            }
        }
    }

    private fun registerEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.event.collect {
                    when (it) {
                        is SignUpEvent.NavigateBack -> navigateBack()
                    }
                }
            }
        }
    }

    private fun registerClickEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding.signUpButton.fetchClick().collect {
                    val email = binding.signupEmailEditText.text.toString()
                    val password = binding.signupPasswordEditText.text.toString()
                    val confirmedPassword = binding.confirmPasswordEditText.text.toString()
                    viewModel.onAction(
                        SignUpAction.CreateAccount(
                            email,
                            password,
                            confirmedPassword
                        )
                    )
                }
            }
        }
    }

    private fun registerFirebaseAuthClientListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                firebaseAuthClient.event.collect {
                    when (it) {
                        is AuthEvent.SignUpUnsuccessful -> showError(it.exception)
                        is AuthEvent.SignedUp -> viewModel.onAction(
                            SignUpAction.SignUp(
                                it.userId,
                                it.email
                            )
                        )
                    }
                }
            }
        }
    }

    private fun navigateBack() {
        Snackbar.make(
            binding.signUpFragmentLayout,
            getString(string.sign_up_success_message),
            Snackbar.LENGTH_LONG
        ).show()
        registrationNavigator.navigateTo(Screen.SignInScreen)
    }

    private fun showError(signUpState: Exception) {
        Snackbar.make(
            binding.signUpFragmentLayout,
            signUpState.message ?: "",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun updateUI(signUpState: SignUpState.InvalidSignUpData) {
        setUpUI()

        if (signUpState.emailError.isNotEmpty()) {
            binding.emailErrorTextView.apply {
                this.isVisible = true
                this.text = signUpState.emailError
            }
        }

        if (signUpState.passwordError.isNotEmpty()) {
            binding.passwordErrorTextView.apply {
                this.isVisible = true
                this.text = signUpState.passwordError
            }
        }
    }

    private fun setUpUI() {
        binding.emailErrorTextView.isVisible = false
        binding.passwordErrorTextView.isVisible = false
        binding.emailErrorTextView.text = ""
        binding.passwordErrorTextView.text = ""
    }
}