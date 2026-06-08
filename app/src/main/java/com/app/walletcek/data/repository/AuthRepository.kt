package com.app.walletcek.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {
    
    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init {
        auth.addAuthStateListener {
            _currentUser.value = it.currentUser
        }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun logout() {
        auth.signOut()
    }

    suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        return try {
            auth.signInWithCredential(credential).await().user
        } catch (e: Exception) {
            null
        }
    }
}

// Extension to use await() with Firebase Tasks if not already available
suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T {
    return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                cont.resume(task.result, null)
            } else {
                cont.resumeWith(Result.failure(task.exception ?: Exception("Unknown error")))
            }
        }
    }
}
