package com.dawidk.common

import android.app.Application

class ApplicationProvider(private val application: Application) {
    fun getApplication() = application
}