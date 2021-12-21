package com.dawidk.registration

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.dawidk.common.registration.AuthEvent
import com.dawidk.common.registration.FirebaseAuthApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class FirebaseAuthClient : FirebaseAuthApi {

    private val auth = Firebase.auth
    private val _event: MutableSharedFlow<AuthEvent> =
        MutableSharedFlow(extraBufferCapacity = 1)
    override val event: SharedFlow<AuthEvent> = _event

    override fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _event.tryEmit(AuthEvent.SignedUp(auth.currentUser?.uid ?: "", email))
                } else {
                    _event.tryEmit(AuthEvent.SignUpUnsuccessful(task.exception ?: Exception()))
                }
            }
    }

    override fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _event.tryEmit(AuthEvent.SignedIn(auth.currentUser?.uid ?: "", email))
                } else {
                    _event.tryEmit(AuthEvent.SignInUnsuccessful(task.exception ?: Exception()))
                }
            }
    }

    override fun signOut() {
        auth.signOut()
        _event.tryEmit(AuthEvent.SignedOut)
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}