package com.happyplaces.domain.usecase

import com.happyplaces.domain.AuthProvider

class SignOutUseCase(private val authProvider: AuthProvider) {
    suspend fun invoke() = authProvider.signOut()
}