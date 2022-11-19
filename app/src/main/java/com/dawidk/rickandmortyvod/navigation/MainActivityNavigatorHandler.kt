package com.dawidk.rickandmortyvod.navigation

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.dawidk.rickandmortyvod.MainActivity
import com.dawidk.common.navigation.MainActivityNavigator

class MainActivityNavigatorHandler(
    private val context: Context
) : MainActivityNavigator {

    override fun startMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ContextCompat.startActivity(context, intent, null)
    }
}