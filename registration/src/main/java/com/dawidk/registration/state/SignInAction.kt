package com.dawidk.registration.state

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task

sealed class SignInAction {
    data class GoogleSignIn(val resultLauncher: ActivityResultLauncher<Intent>) : SignInAction()
    data class EmailSignIn(val email: String, val password: String) : SignInAction()
    object GetLastSignedIn : SignInAction()
    data class SignInResult(val completedTask: Task<GoogleSignInAccount>) : SignInAction()
    data class HandleError(val error: Throwable) : SignInAction()
    object NavigateToSignUpScreen : SignInAction()
    data class SaveCredentials(val userId: String, val email: String) : SignInAction()
    object ClearErrorMessages : SignInAction()
}