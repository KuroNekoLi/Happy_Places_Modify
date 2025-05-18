package com.happyplaces.domain.repository

import com.happyplaces.domain.model.User
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun addUser(user: User): Flow<ApiResource<Unit>>
    fun isUserProfileCompleted(id: String): Flow<ApiResource<Boolean>>
    fun getCurrentUser(): Flow<ApiResource<User?>>
}