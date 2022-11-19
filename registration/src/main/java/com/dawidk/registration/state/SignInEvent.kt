package com.dawidk.registration.state

sealed class SignInEvent {
    object NavigateToHomeScreen : SignInEvent()
    object NavigateToSignUpScreen : SignInEvent()
}