package com.happyplaces.domain.usecase

import com.happyplaces.domain.repository.UserRepository

/**
 * Use Case：訂閱並取得當前使用者資訊的流。
 */
class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) {
    /** 返回一個 Flow，持續發出當前使用者狀態 */
    operator fun invoke() = userRepository.getCurrentUser()
}