package com.happyplaces.data.repository

import com.happyplaces.data.datasource.remote.PlaceService
import com.happyplaces.domain.model.User
import com.happyplaces.domain.model.toUserDto
import com.happyplaces.domain.repository.UserRepository
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(private val placeService: PlaceService) : UserRepository {
    override suspend fun addUser(user: User): Flow<ApiResource<Unit>> = flow {
        emit(ApiResource.Loading())
        try {
            placeService.addUser(user.toUserDto())
            emit(ApiResource.Success(Unit))
        } catch (e: Exception) {
            emit(ApiResource.Error(e.message ?: "未知錯誤"))
        }
    }

    override suspend fun isUserProfileCompleted(id: String): Flow<ApiResource<Boolean>> = flow {
        emit(ApiResource.Loading())
        try {
            val isCompleted = placeService.isUserProfileCompleted(id)
            emit(ApiResource.Success(isCompleted))
        } catch (e: Exception) {
            emit(ApiResource.Error(e.message ?: "未知錯誤"))
        }
    }
}