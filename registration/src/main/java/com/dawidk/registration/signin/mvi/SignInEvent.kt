package com.dawidk.registration.signin.mvi

import com.dawidk.common.mvi.ViewEvent

sealed class SignInEvent: ViewEvent {
    object NavigateToHomeScreen : SignInEvent()
    object NavigateToSignUpScreen : SignInEvent()
}