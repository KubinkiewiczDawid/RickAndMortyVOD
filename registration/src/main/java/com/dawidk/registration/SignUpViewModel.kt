package com.dawidk.registration

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dawidk.common.registration.FirebaseAuthApi
import com.dawidk.core.datastore.UserCredentialsDataStoreRepository
import com.dawidk.core.domain.model.AccountInfo
import com.dawidk.registration.R.string
import com.dawidk.registration.state.SignUpAction
import com.dawidk.registration.state.SignUpEvent
import com.dawidk.registration.state.SignUpState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

const val FIREBASE_PASSWORD_LENGTH = 6

class SignUpViewModel(
    application: Application,
    private val userCredentialsDataStoreRepository: UserCredentialsDataStoreRepository,
    private val firebaseAuthClient: FirebaseAuthApi
) : AndroidViewModel(application) {

    private val _state: MutableStateFlow<SignUpState> =
        MutableStateFlow(
            SignUpState.Idle
        )
    val state: StateFlow<SignUpState> = _state
    private val _event: MutableSharedFlow<SignUpEvent> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val event: SharedFlow<SignUpEvent> = _event

    fun onAction(action: SignUpAction) {
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
            is SignUpAction.ClearErrorMessages -> clearErrorMessages()
        }
    }

    private fun createAccount(email: String, password: String, confirmedPassword: String) {
        if (isSignUpDataValid(email, password, confirmedPassword)) {
            firebaseAuthClient.createAccount(email, password)
        }
    }

    private fun clearErrorMessages() {
        _state.value = SignUpState.Idle
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
        _event.tryEmit(SignUpEvent.NavigateBack)
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
            _state.value = SignUpState.InvalidSignUpData(
                emailError,
                passwordError
            )
            return false
        }
        return true
    }

    private fun validateEmail(email: String): String {
        return if (email.isEmpty()) {
            getApplication<Application>().resources.getString(string.email_empty_error_message)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            getApplication<Application>().resources.getString(string.incorrect_email_error_message)
        } else {
            ""
        }
    }

    private fun validatePassword(password: String, confirmedPassword: String): String {
        return if (password.isEmpty()) {
            getApplication<Application>().resources.getString(string.password_empty_error_message)
        } else if (password.length < FIREBASE_PASSWORD_LENGTH) {
            getApplication<Application>().resources.getString(string.password_length_error_message)
        } else if (password != confirmedPassword) {
            getApplication<Application>().resources.getString(string.different_passwords_error_message)
        } else {
            ""
        }
    }
}