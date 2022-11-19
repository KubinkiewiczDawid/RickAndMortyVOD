package com.dawidk.registration.state

sealed class SignUpState {
    object Idle : SignUpState()
    data class Error(val exception: Throwable) : SignUpState()
    data class InvalidSignUpData(val emailError: String, val passwordError: String) : SignUpState()
}