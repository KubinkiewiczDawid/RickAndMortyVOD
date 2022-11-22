package com.dawidk.registration.signin.mvi

import com.dawidk.common.mvi.ViewState

sealed class SignInState: ViewState {
    object Idle : SignInState()
    data class Error(val exception: Throwable) : SignInState()
    data class CredentialsError(val exception: Exception) : SignInState()
    data class InvalidSignInData(val emailError: String, val passwordError: String) : SignInState()
}