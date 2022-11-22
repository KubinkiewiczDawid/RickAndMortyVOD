package com.dawidk.registration.signup.mvi

import com.dawidk.common.mvi.ViewState

sealed class SignUpState: ViewState {
    object Idle : SignUpState()
    data class Error(val exception: Exception) : SignUpState()
    data class InvalidSignUpData(val emailError: String, val passwordError: String) : SignUpState()
}