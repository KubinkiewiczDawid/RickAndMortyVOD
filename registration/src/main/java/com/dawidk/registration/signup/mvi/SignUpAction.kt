package com.dawidk.registration.signup.mvi

import com.dawidk.common.mvi.ViewAction

sealed class SignUpAction : ViewAction {
    data class SignUp(val userId: String, val email: String) : SignUpAction()
    data class CreateAccount(
        val email: String,
        val password: String,
        val confirmedPassword: String
    ) : SignUpAction()

    data class SignUpUnsuccessful(val exception: Exception) : SignUpAction()
    object ClearErrorMessages : SignUpAction()
}