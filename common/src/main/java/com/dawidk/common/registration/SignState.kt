package com.dawidk.common.registration

sealed class SignState {
    object SignedIn : SignState()
    object SignedOut : SignState()
}