package com.dawidk.registration.utils

import android.content.res.Resources
import com.dawidk.registration.R

class ErrorMessageProvider(resources: Resources) {

    val emailEmpty = resources.getString(R.string.email_empty_error_message)
    val incorrectEmail = resources.getString(R.string.incorrect_email_error_message)
    val passwordEmpty = resources.getString(R.string.password_empty_error_message)
    val passwordLength = resources.getString(R.string.password_length_error_message)
    val differentPasswords = resources.getString(R.string.different_passwords_error_message)

}