package com.happyplaces.data.repository

import com.happyplaces.data.datasource.remote.PlaceService
import com.happyplaces.data.datasource.remote.toUser
import com.happyplaces.domain.model.User
import com.happyplaces.domain.model.toUserDto
import com.happyplaces.domain.repository.UserRepository
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(private val placeService: PlaceService) : UserRepository {
    override fun addUser(user: User): Flow<ApiResource<Unit>> = flow {
        emit(ApiResource.Loading())
        try {
            placeService.addUser(user.toUserDto())
            emit(ApiResource.Success(Unit))
        } catch (e: Exception) {
            emit(ApiResource.Error(e.message ?: "未知錯誤"))
        }
    }

    override fun isUserProfileCompleted(id: String): Flow<ApiResource<Boolean>> = flow {
        emit(ApiResource.Loading())
        try {
            val isCompleted = placeService.isUserProfileCompleted(id)
            emit(ApiResource.Success(isCompleted))
        } catch (e: Exception) {
            emit(ApiResource.Error(e.message ?: "未知錯誤"))
        }
    }

    override fun getCurrentUser(): Flow<ApiResource<User?>> = flow {
        emit(ApiResource.Loading())
        try {
            val userDto = placeService.getCurrentUser()
            emit(ApiResource.Success(userDto?.toUser()))
        } catch (e: Exception) {
            emit(ApiResource.Error(e.message ?: "未知錯誤"))
        }
    }

    override fun checkAccountIdExists(accountId: String): Flow<ApiResource<Boolean>> = flow {
        emit(ApiResource.Loading())
        try {
            val exists = placeService.checkAccountIdExists(accountId)
            emit(ApiResource.Success(exists))
        } catch (e: Exception) {
            emit(ApiResource.Error(e.message ?: "檢查帳號時發生錯誤"))
        }
    }

    override fun updateUser(user: User): Flow<ApiResource<Unit>> = flow {
        emit(ApiResource.Loading())
        try {
            placeService.updateUser(user.toUserDto())
            emit(ApiResource.Success(Unit))
        } catch (e: Exception) {
            emit(ApiResource.Error(e.message ?: "更新用戶資訊時發生錯誤"))
        }
    }
}
