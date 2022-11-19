package com.dawidk.registration

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.dawidk.common.registration.GoogleClientApi
import com.dawidk.common.registration.SignState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class GoogleClientApiWrapper(
    val context: Context
) : GoogleClientApi {

    private val _event: MutableSharedFlow<SignState> =
        MutableSharedFlow(extraBufferCapacity = 1)
    override val event: SharedFlow<SignState> = _event
    private val gso: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    private val mGoogleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
    override fun signOut(activity: Activity) {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(activity) {
                _event.tryEmit(SignState.SignedOut)
            }
    }

    override fun getSignedInIntent(): Intent {
        return mGoogleSignInClient.signInIntent
    }
}