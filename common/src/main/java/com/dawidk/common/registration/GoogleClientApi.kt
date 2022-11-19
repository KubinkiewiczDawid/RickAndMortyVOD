package com.dawidk.common.registration

import android.app.Activity
import android.content.Intent
import kotlinx.coroutines.flow.SharedFlow

interface GoogleClientApi {

    val event: SharedFlow<SignState>
    fun signOut(activity: Activity)
    fun getSignedInIntent(): Intent
}