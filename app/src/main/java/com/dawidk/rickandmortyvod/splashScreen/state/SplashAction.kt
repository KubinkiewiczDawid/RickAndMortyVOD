package com.dawidk.rickandmortyvod.splashScreen.state

import com.dawidk.core.domain.model.AccountInfo

sealed class SplashAction {
    data class Init(val data: AccountInfo) : SplashAction()
    object PrepareData : SplashAction()
}