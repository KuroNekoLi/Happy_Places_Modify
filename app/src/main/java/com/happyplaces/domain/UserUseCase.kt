package com.happyplaces.domain

import com.happyplaces.domain.model.User
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow

class UserUseCase(private val repository: UserRepository) {
    suspend fun addUser(user: User): Flow<ApiResource<Unit>> = repository.addUser(user)
    suspend fun isUserProfileCompleted(id: String): Flow<ApiResource<Boolean>> =
        repository.isUserProfileCompleted(id)
}