package com.happyplaces.domain

class SignOutUseCase(private val authProvider: AuthProvider) {
    suspend fun invoke() = authProvider.signOut()
}