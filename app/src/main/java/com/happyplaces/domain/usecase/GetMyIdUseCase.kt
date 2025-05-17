package com.happyplaces.domain.usecase

import com.happyplaces.domain.AuthProvider

class GetMyIdUseCase(
    private val authProvider: AuthProvider
) {
    suspend fun invoke(): String? = authProvider.getMyId()
}