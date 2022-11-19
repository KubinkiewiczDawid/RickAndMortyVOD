package com.dawidk.settings.navigation

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.dawidk.common.navigation.SettingsActivityNavigator
import com.dawidk.settings.SettingsActivity

class SettingsActivityNavigatorHandler(
    private val context: Context
) : SettingsActivityNavigator {

    override fun startSettingsActivity() {
        val intent = Intent(context, SettingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ContextCompat.startActivity(context, intent, null)
    }
}