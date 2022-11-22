package com.dawidk.registration.navigation

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.dawidk.common.navigation.RegistrationActivityNavigator
import com.dawidk.registration.RegistrationActivity

class RegistrationActivityNavigatorHandler(
    private val context: Context
) : RegistrationActivityNavigator {

    override fun startRegistrationActivity() {
        val intent = Intent(context, RegistrationActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        ContextCompat.startActivity(context, intent, null)
    }
}