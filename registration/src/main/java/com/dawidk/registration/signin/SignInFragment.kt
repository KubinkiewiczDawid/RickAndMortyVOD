package com.dawidk.registration.signin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.mvi.BaseFragment
import com.dawidk.common.registration.AuthEvent
import com.dawidk.common.registration.FirebaseAuthApi
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.common.utils.fetchClick
import com.dawidk.registration.R
import com.dawidk.registration.R.string
import com.dawidk.registration.databinding.FragmentSignInBinding
import com.dawidk.registration.navigation.RegistrationNavigator
import com.dawidk.registration.navigation.Screen
import com.dawidk.registration.signin.mvi.SignInAction
import com.dawidk.registration.signin.mvi.SignInEvent
import com.dawidk.registration.signin.mvi.SignInState
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInFragment :
    BaseFragment<SignInEvent, SignInAction, SignInState, SignInViewModel, FragmentSignInBinding>(R.layout.fragment_sign_in) {

    override val viewModel by viewModel<SignInViewModel>()
    override val binding by viewBinding(FragmentSignInBinding::bind)
    override val networkMonitor: NetworkMonitor by inject()
    private val firebaseAuthClient: FirebaseAuthApi by inject()
    private val registrationNavigator: RegistrationNavigator by inject()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registrationNavigator.navController = findNavController()

        registerClickEventListener()
        registerFirebaseAuthClientListener()

        initResultLauncher()
    }

    override fun handleEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.NavigateToHomeScreen -> navigateToHomeFragment()
            is SignInEvent.NavigateToSignUpScreen -> navigateToSignUpFragment()
        }
    }

    override fun handleState(state: SignInState) {
        when (state) {
            is SignInState.Idle -> {
                setUpUI()
            }
            is SignInState.Error -> showError(state)
            is SignInState.InvalidSignInData -> updateUI(
                state
            )
            is SignInState.CredentialsError -> showCredentialsError(state.exception)
        }
    }

    override fun onDataLoadingException() {
        viewModel.onAction(SignInAction.GetLastSignedIn)
    }

    override fun onStart() {
        super.onStart()
        viewModel.onAction(SignInAction.GetLastSignedIn)
    }

    override fun onStop() {
        super.onStop()
        viewModel.onAction(SignInAction.ClearErrorMessages)
    }

    override fun onDestroy() {
        super.onDestroy()
        resultLauncher.unregister()
    }

    private fun registerClickEventListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding.emailSignInButton.fetchClick().collect {
                    val email = binding.emailEditText.text.toString()
                    val password = binding.passwordEditText.text.toString()
                    viewModel.onAction(
                        SignInAction.EmailSignIn(
                            email,
                            password
                        )
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding.googleSignInButton.fetchClick().collect {
                    viewModel.onAction(
                        SignInAction.GoogleSignIn(
                            resultLauncher
                        )
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding.signUpLink.fetchClick().collect {
                    viewModel.onAction(SignInAction.NavigateToSignUpScreen)
                }
            }
        }
    }

    private fun registerFirebaseAuthClientListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                firebaseAuthClient.event.collect {
                    when (it) {
                        is AuthEvent.SignInUnsuccessful -> viewModel.onAction(
                            SignInAction.SignInUnsuccessful(it.exception)
                        )
                        is AuthEvent.SignedIn -> viewModel.onAction(
                            SignInAction.SaveCredentials(
                                it.userId,
                                it.email
                            )
                        )
                    }
                }
            }
        }
    }

    private fun initResultLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.let {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(it)
                        viewModel.onAction(
                            SignInAction.SignInResult(
                                task
                            )
                        )
                    } ?: run {
                        viewModel.onAction(
                            SignInAction.HandleError(
                                Throwable(
                                    resources.getString(
                                        string.sign_in_error_msg
                                    )
                                )
                            )
                        )
                    }
                }
            }
    }

    private fun navigateToHomeFragment() {
        registrationNavigator.navigateTo(Screen.HomeScreen)
        activity?.finish()
    }

    private fun navigateToSignUpFragment() {
        registrationNavigator.navigateTo(Screen.SignUpScreen)
    }

    private fun showError(state: SignInState.Error) {
        ErrorDialogFragment.show(childFragmentManager, state.exception)
    }

    private fun updateUI(signInState: SignInState.InvalidSignInData) {
        setUpUI()

        if (signInState.emailError.isNotEmpty()) {
            binding.signInEmailErrorTextView.apply {
                this.isVisible = true
                this.text = signInState.emailError
            }
        }

        if (signInState.passwordError.isNotEmpty()) {
            binding.signInPasswordErrorTextView.apply {
                this.isVisible = true
                this.text = signInState.passwordError
            }
        }
    }

    private fun setUpUI() {
        binding.signInEmailErrorTextView.isVisible = false
        binding.signInPasswordErrorTextView.isVisible = false
        binding.signInEmailErrorTextView.text = ""
        binding.signInPasswordErrorTextView.text = ""
    }

    private fun showCredentialsError(exception: Exception) {
        val emailError = if (exception is FirebaseAuthInvalidUserException) {
            requireContext().resources.getString(string.no_user_error_message)
        } else {
            ""
        }
        val passwordError =
            if (exception is FirebaseAuthInvalidCredentialsException) {
                requireContext().resources.getString(string.invalid_credentials_error_message)
            } else {
                ""
            }

        if (emailError.isNotEmpty() || passwordError.isNotEmpty()) {
            updateUI(
                SignInState.InvalidSignInData(
                    emailError,
                    passwordError
                )
            )
        } else {
            Snackbar.make(
                binding.signInLayout,
                exception.message ?: "",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}