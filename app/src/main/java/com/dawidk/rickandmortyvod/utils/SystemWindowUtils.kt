package com.dawidk.rickandmortyvod.utils

import android.os.Build
import android.view.View
import android.view.Window

fun Window.fitSystemWindow(fitToSystemWindow: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        setDecorFitsSystemWindows(fitToSystemWindow)
    } else {
        if (fitToSystemWindow) {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        } else {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }
}