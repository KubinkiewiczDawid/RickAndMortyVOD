package com.dawidk.registration.signin

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.viewModelScope
import com.dawidk.common.ApplicationProvider
import com.dawidk.common.mvi.BaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.dawidk.common.registration.FirebaseAuthApi
import com.dawidk.common.registration.GoogleClientApi
import com.dawidk.core.datastore.UserCredentialsDataStoreRepository
import com.dawidk.core.domain.mappers.mapToAccountInfo
import com.dawidk.core.domain.model.AccountInfo
import com.dawidk.registration.signin.mvi.SignInAction
import com.dawidk.registration.signin.mvi.SignInEvent
import com.dawidk.registration.signin.mvi.SignInState
import com.dawidk.registration.utils.ErrorMessageProvider
import kotlinx.coroutines.launch

class SignInViewModel(
    private val userCredentialsDataStoreRepository: UserCredentialsDataStoreRepository,
    private val googleClientApi: GoogleClientApi,
    private val firebaseAuthClient: FirebaseAuthApi,
    private val applicationProvider: ApplicationProvider,
    private val errorMessageProvider: ErrorMessageProvider
) : BaseViewModel<SignInEvent, SignInAction, SignInState>(SignInState.Idle) {

    override fun onAction(action: SignInAction) {
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
            is SignInAction.HandleError -> updateState(SignInState.Error(action.error))
            is SignInAction.NavigateToSignUpScreen -> navigateToSignUpScreen()
            is SignInAction.SaveCredentials -> viewModelScope.launch {
                onEmailSignUpSuccessful(
                    action.userId,
                    action.email
                )
            }
            is SignInAction.SignInUnsuccessful -> updateState(SignInState.CredentialsError(action.exception))
            is SignInAction.ClearErrorMessages -> clearErrorMessages()
        }
    }

    private fun getLastSignedIn() {
        GoogleSignIn.getLastSignedInAccount(applicationProvider.getApplication())?.let {
            viewModelScope.launch { onRegisterComplete(it) }
        }
    }

    private fun clearErrorMessages() {
        updateState(SignInState.Idle)
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

        emitEvent(SignInEvent.NavigateToHomeScreen)
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
            errorMessageProvider.emailEmpty
        } else ""
        val passwordError = if (password.isEmpty()) {
            errorMessageProvider.passwordEmpty
        } else ""

        if (emailError.isNotEmpty() || passwordError.isNotEmpty()) {
            updateState(
                SignInState.InvalidSignInData(
                    emailError,
                    passwordError
                )
            )
            return false
        }

        return true
    }

    private suspend fun onEmailSignUpSuccessful(userId: String, email: String) {
        viewModelScope.launch { saveUserCredentials(AccountInfo(userId, email, email)) }.join()
        emitEvent(SignInEvent.NavigateToHomeScreen)
    }

    private fun navigateToSignUpScreen() {
        emitEvent(SignInEvent.NavigateToSignUpScreen)
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