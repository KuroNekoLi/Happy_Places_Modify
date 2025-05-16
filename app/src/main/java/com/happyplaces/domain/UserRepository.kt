package com.happyplaces.domain

import com.happyplaces.domain.model.User
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun addUser(user: User): Flow<ApiResource<Unit>>
    suspend fun isUserProfileCompleted(id: String): Flow<ApiResource<Boolean>>
}