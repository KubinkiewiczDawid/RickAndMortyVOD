package com.dawidk.rickandmortyvod.splashScreen.state

sealed class SplashEvent {
    object NavigateToNextScreen : SplashEvent()
}
