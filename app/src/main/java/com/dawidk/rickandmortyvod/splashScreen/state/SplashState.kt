package com.dawidk.rickandmortyvod.splashScreen.state

sealed class SplashState {
    object Loading : SplashState()
    object SignedIn : SplashState()
    object NotSignedIn : SplashState()
    data class Error(val exception: Throwable) : SplashState()
}