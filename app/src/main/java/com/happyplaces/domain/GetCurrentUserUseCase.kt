package com.happyplaces.domain

import com.happyplaces.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Use Case：訂閱並取得當前使用者資訊的流。
 */
class GetCurrentUserUseCase(
    private val authProvider: AuthProvider
) {
    /** 返回一個 Flow，持續發出當前使用者狀態 */
    operator fun invoke(): Flow<User?> =
        authProvider.getCurrentUser()
}