package com.dawidk.common.registration

import kotlinx.coroutines.flow.SharedFlow

interface FirebaseAuthApi {

    val event: SharedFlow<AuthEvent>
    fun createAccount(email: String, password: String)
    fun signInWithEmail(email: String, password: String)
    fun signOut()
    fun isUserLoggedIn(): Boolean
}