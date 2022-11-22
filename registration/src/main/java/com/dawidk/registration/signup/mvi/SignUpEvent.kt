package com.dawidk.registration.signup.mvi

import com.dawidk.common.mvi.ViewEvent

sealed class SignUpEvent: ViewEvent {
    object NavigateBack : SignUpEvent()
}