package com.dawidk.registration.state

sealed class SignUpEvent {
    object NavigateBack : SignUpEvent()
}