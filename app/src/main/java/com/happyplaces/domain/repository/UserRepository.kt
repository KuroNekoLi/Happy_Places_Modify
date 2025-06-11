package com.happyplaces.domain.repository

import com.happyplaces.domain.model.User
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun addUser(user: User): Flow<ApiResource<Unit>>
    fun isUserProfileCompleted(id: String): Flow<ApiResource<Boolean>>
    fun getCurrentUser(): Flow<ApiResource<User?>>

    /**
     * 檢查帳號 ID 是否已存在
     * @param accountId 要檢查的帳號 ID
     * @return Flow<ApiResource<Boolean>> 如果存在返回 true，否則返回 false
     */
    fun checkAccountIdExists(accountId: String): Flow<ApiResource<Boolean>>

    /**
     * 更新用戶資訊
     * @param user 用戶物件
     * @return Flow<ApiResource<Unit>>
     */
    fun updateUser(user: User): Flow<ApiResource<Unit>>
}
