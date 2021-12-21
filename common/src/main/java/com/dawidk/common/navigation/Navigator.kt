package com.dawidk.common.navigation

import androidx.navigation.NavController

interface Navigator {

    val navController: NavController?
    fun navigateTo(screen: NavigatorScreen)
}