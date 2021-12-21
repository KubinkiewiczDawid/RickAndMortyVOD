package com.dawidk.registration.state

sealed class SignInState {
    object Idle : SignInState()
    data class Error(val exception: Throwable) : SignInState()
    data class CredentialsError(val exception: Throwable) : SignInState()
    data class InvalidSignInData(val emailError: String, val passwordError: String) : SignInState()
}