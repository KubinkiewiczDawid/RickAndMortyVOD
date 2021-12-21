package com.dawidk.registration.state

sealed class SignUpAction {
    data class SignUp(val userId: String, val email: String) : SignUpAction()
    data class CreateAccount(
        val email: String,
        val password: String,
        val confirmedPassword: String
    ) : SignUpAction()

    object ClearErrorMessages : SignUpAction()
}