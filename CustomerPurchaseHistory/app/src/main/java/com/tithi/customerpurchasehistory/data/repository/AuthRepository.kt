package com.tithi.customerpurchasehistory.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun login(email: String, password: String): Result<FirebaseUser> =
        suspendCancellableCoroutine { cont ->
            auth.signInWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null) {
                        cont.resume(Result.success(user))
                    } else {
                        cont.resume(Result.failure(Exception("Authentication failed. Please try again.")))
                    }
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(Exception(mapAuthError(e))))
                }
        }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser> =
        suspendCancellableCoroutine { cont ->
            auth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null) {
                        cont.resume(Result.success(user))
                    } else {
                        cont.resume(Result.failure(Exception("Account creation failed. Please try again.")))
                    }
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(Exception(mapAuthError(e))))
                }
        }

    suspend fun sendPasswordReset(email: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            auth.sendPasswordResetEmail(email.trim())
                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { e -> cont.resume(Result.failure(Exception(mapAuthError(e)))) }
        }

    fun signOut() {
        auth.signOut()
    }

    private fun mapAuthError(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthInvalidUserException -> "No account found with this email address."
            is FirebaseAuthInvalidCredentialsException -> "Invalid email or password. Please check and try again."
            is FirebaseAuthUserCollisionException -> "An account with this email already exists."
            is FirebaseAuthWeakPasswordException -> "Password is too weak. Please use at least 6 characters."
            is FirebaseNetworkException -> "Network error. Please check your internet connection."
            else -> exception.localizedMessage ?: "Authentication error occurred."
        }
    }
}
