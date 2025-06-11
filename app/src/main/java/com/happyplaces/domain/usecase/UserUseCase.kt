package com.happyplaces.domain.usecase

import com.happyplaces.domain.model.User
import com.happyplaces.domain.repository.UserRepository
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow

class UserUseCase(private val repository: UserRepository) {
    suspend fun addUser(user: User): Flow<ApiResource<Unit>> = repository.addUser(user)

    suspend fun isUserProfileCompleted(id: String): Flow<ApiResource<Boolean>> =
        repository.isUserProfileCompleted(id)

    /**
     * 檢查帳號 ID 是否已存在
     * @param accountId 要檢查的帳號 ID
     * @return Flow<ApiResource<Boolean>> 如果存在返回 true，否則返回 false
     */
    suspend fun checkAccountIdExists(accountId: String): Flow<ApiResource<Boolean>> =
        repository.checkAccountIdExists(accountId)

    /**
     * 更新用戶資訊
     * @param user 用戶物件
     * @return Flow<ApiResource<Unit>>
     */
    suspend fun updateUser(user: User): Flow<ApiResource<Unit>> = repository.updateUser(user)
}
