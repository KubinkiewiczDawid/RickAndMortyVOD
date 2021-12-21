package com.dawidk.registration

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.dawidk.common.registration.FirebaseAuthApi
import com.dawidk.common.registration.GoogleClientApi
import com.dawidk.core.datastore.UserCredentialsDataStoreRepository
import com.dawidk.core.domain.mappers.mapToAccountInfo
import com.dawidk.core.domain.model.AccountInfo
import com.dawidk.registration.R.string
import com.dawidk.registration.state.SignInAction
import com.dawidk.registration.state.SignInEvent
import com.dawidk.registration.state.SignInState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    application: Application,
    private val userCredentialsDataStoreRepository: UserCredentialsDataStoreRepository,
    private val googleClientApi: GoogleClientApi,
    private val firebaseAuthClient: FirebaseAuthApi
) : AndroidViewModel(application) {

    private val _state: MutableStateFlow<SignInState> =
        MutableStateFlow(
            SignInState.Idle
        )
    val state: StateFlow<SignInState> = _state
    private val _event: MutableSharedFlow<SignInEvent> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<SignInEvent> = _event

    fun onAction(action: SignInAction) {
        when (action) {
            is SignInAction.GoogleSignIn -> signInWithGoogle(action.resultLauncher)
            is SignInAction.EmailSignIn -> signInWithEmail(
                action.email,
                action.password
            )
            is SignInAction.GetLastSignedIn -> getLastSignedIn()
            is SignInAction.SignInResult -> handleSignInResult(
                action.completedTask
            )
            is SignInAction.HandleError -> _state.value =
                SignInState.Error(action.error)
            is SignInAction.NavigateToSignUpScreen -> navigateToSignUpScreen()
            is SignInAction.SaveCredentials -> viewModelScope.launch {
                onEmailSignUpSuccessful(
                    action.userId,
                    action.email
                )
            }
            is SignInAction.ClearErrorMessages -> clearErrorMessages()
        }
    }

    private fun getLastSignedIn() {
        GoogleSignIn.getLastSignedInAccount(getApplication())?.let {
            viewModelScope.launch { onRegisterComplete(it) }
        }
    }

    private fun clearErrorMessages() {
        _state.value = SignInState.Idle
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            viewModelScope.launch { onRegisterComplete(account) }
        } catch (e: ApiException) {
            Log.e("register", "signInResult:failed code=" + e.stackTraceToString())
        }
    }

    private suspend fun onRegisterComplete(googleSignInAccount: GoogleSignInAccount) {
        clearUserCredentials()
        viewModelScope.launch { saveUserCredentials(googleSignInAccount.mapToAccountInfo()) }.join()

        _event.tryEmit(SignInEvent.NavigateToHomeScreen)
    }

    private fun signInWithGoogle(resultLauncher: ActivityResultLauncher<Intent>) {
        val signInIntent: Intent = googleClientApi.getSignedInIntent()

        resultLauncher.launch(signInIntent)
    }

    private fun signInWithEmail(email: String, password: String) {
        if (areCredentialsNotEmpty(email, password)) {
            firebaseAuthClient.signInWithEmail(email, password)
        }
    }

    private fun areCredentialsNotEmpty(
        email: String,
        password: String
    ): Boolean {
        val emailError = if (email.isEmpty()) {
            getApplication<Application>().resources.getString(string.email_empty_error_message)
        } else ""
        val passwordError = if (password.isEmpty()) {
            getApplication<Application>().resources.getString(string.password_empty_error_message)
        } else ""

        if (emailError.isNotEmpty() || passwordError.isNotEmpty()) {
            _state.value = SignInState.InvalidSignInData(
                emailError,
                passwordError
            )
            return false
        }

        return true
    }

    private suspend fun onEmailSignUpSuccessful(userId: String, email: String) {
        viewModelScope.launch { saveUserCredentials(AccountInfo(userId, email, email)) }.join()
        _event.tryEmit(SignInEvent.NavigateToHomeScreen)
    }

    private fun navigateToSignUpScreen() {
        _event.tryEmit(SignInEvent.NavigateToSignUpScreen)
    }

    private suspend fun saveUserCredentials(accountInfo: AccountInfo) {
        viewModelScope.launch {
            userCredentialsDataStoreRepository.updateUserEmail(accountInfo.email)
            userCredentialsDataStoreRepository.updateUserDisplayName(accountInfo.displayName)
            userCredentialsDataStoreRepository.updateUserId(accountInfo.id)
        }.join()
    }

    private fun clearUserCredentials() {
        viewModelScope.launch {
            userCredentialsDataStoreRepository.clearUserCredentials()
        }
    }
}