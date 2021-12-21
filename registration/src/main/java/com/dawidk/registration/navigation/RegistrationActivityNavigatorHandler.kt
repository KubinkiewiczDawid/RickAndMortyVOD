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
        val intent = Intent(context, RegistrationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ContextCompat.startActivity(context, intent, null)
    }
}