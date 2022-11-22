package com.dawidk.registration.signup

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.dawidk.common.mvi.BaseViewModel
import com.dawidk.common.registration.FirebaseAuthApi
import com.dawidk.core.datastore.UserCredentialsDataStoreRepository
import com.dawidk.core.domain.model.AccountInfo
import com.dawidk.registration.signup.mvi.SignUpAction
import com.dawidk.registration.signup.mvi.SignUpEvent
import com.dawidk.registration.signup.mvi.SignUpState
import com.dawidk.registration.utils.ErrorMessageProvider
import kotlinx.coroutines.launch

const val FIREBASE_PASSWORD_LENGTH = 6

class SignUpViewModel(
    private val userCredentialsDataStoreRepository: UserCredentialsDataStoreRepository,
    private val firebaseAuthClient: FirebaseAuthApi,
    private val errorMessageProvider: ErrorMessageProvider
) : BaseViewModel<SignUpEvent, SignUpAction, SignUpState>(SignUpState.Idle) {

    override fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.CreateAccount -> createAccount(
                action.email,
                action.password,
                action.confirmedPassword
            )
            is SignUpAction.SignUp -> viewModelScope.launch {
                signUp(
                    action
                )
            }
            is SignUpAction.SignUpUnsuccessful -> updateState(SignUpState.Error(action.exception))
            is SignUpAction.ClearErrorMessages -> clearErrorMessages()
        }
    }

    private fun createAccount(email: String, password: String, confirmedPassword: String) {
        if (isSignUpDataValid(email, password, confirmedPassword)) {
            firebaseAuthClient.createAccount(email, password)
        }
    }

    private fun clearErrorMessages() {
        updateState(SignUpState.Idle)
    }

    private suspend fun signUp(user: SignUpAction.SignUp) {
        clearUserCredentials()
        viewModelScope.launch {
            saveUserCredentials(
                AccountInfo(
                    user.userId,
                    user.email,
                    user.email
                )
            )
        }.join()
        emitEvent(SignUpEvent.NavigateBack)
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

    private fun isSignUpDataValid(
        email: String,
        password: String,
        confirmedPassword: String
    ): Boolean {
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password, confirmedPassword)

        if (emailError.isNotEmpty() || passwordError.isNotEmpty()) {
            updateState(
                SignUpState.InvalidSignUpData(
                    emailError,
                    passwordError
                )
            )
            return false
        }
        return true
    }

    private fun validateEmail(email: String): String {
        return if (email.isEmpty()) {
            errorMessageProvider.emailEmpty
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessageProvider.incorrectEmail
        } else {
            ""
        }
    }

    private fun validatePassword(password: String, confirmedPassword: String): String {
        return if (password.isEmpty()) {
            errorMessageProvider.passwordEmpty
        } else if (password.length < FIREBASE_PASSWORD_LENGTH) {
            errorMessageProvider.passwordLength
        } else if (password != confirmedPassword) {
            errorMessageProvider.differentPasswords
        } else {
            ""
        }
    }
}