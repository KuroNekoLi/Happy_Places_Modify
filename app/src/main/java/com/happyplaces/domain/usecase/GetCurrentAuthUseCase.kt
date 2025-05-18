package com.happyplaces.domain.usecase

import com.happyplaces.domain.AuthProvider
import com.happyplaces.domain.model.User
import kotlinx.coroutines.flow.Flow

class GetCurrentAuthUseCase(private val authProvider: AuthProvider) {
    /** 返回一個 Flow，持續發出當前使用者狀態 */
    operator fun invoke(): Flow<User?> =
        authProvider.getCurrentUser()

}