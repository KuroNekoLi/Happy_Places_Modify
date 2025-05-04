package com.happyplaces.domain


import com.happyplaces.domain.model.HappyPlaceAuthResult
import com.happyplaces.domain.model.HappyPlaceUser
import kotlinx.coroutines.flow.Flow

/**
 * 認證提供者介面：定義所有用於登入／登出及當前使用者查詢的通用 API。
 */
interface AuthProvider {
    /** 觸發登入流程，回傳認證結果（含 isNewUser） */
    suspend fun signIn(): HappyPlaceAuthResult

    /** 登出當前使用者 */
    suspend fun signOut()

    /** 取得目前已登入的使用者 (若無則為 null) */
    fun getCurrentUser(): Flow<HappyPlaceUser?>
}