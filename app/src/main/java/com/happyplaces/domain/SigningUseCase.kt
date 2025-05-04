package com.happyplaces.domain

import com.happyplaces.domain.model.HappyPlaceAuthResult

class SigningUseCase(private val authProvider: AuthProvider) {
    suspend fun invoke(): HappyPlaceAuthResult = authProvider.signIn()
}