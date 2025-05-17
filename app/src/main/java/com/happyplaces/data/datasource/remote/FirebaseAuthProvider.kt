package com.happyplaces.data.datasource.remote

import com.google.firebase.auth.FirebaseAuth
import com.happyplaces.domain.AuthProvider
import com.happyplaces.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseAuthProvider : AuthProvider {
    private val firebaseAuth = FirebaseAuth.getInstance()

    override suspend fun signIn(): User? {
        throw UnsupportedOperationException(
            "請在 View/UseCase 層啟動 FirebaseUI 並將結果傳入"
        )
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(
                auth.currentUser?.let {
                    User(
                        id = it.uid,
                        name = it.displayName.orEmpty(),
                        avatarUrl = it.photoUrl.toString(),
                        email = it.email.orEmpty(),
                        profileCompleted = true,
                        isAnonymous = false
                    )
                }
            )
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }
}