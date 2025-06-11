package com.happyplaces.presentation.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyplaces.domain.model.User
import com.happyplaces.domain.usecase.GetCurrentAuthUseCase
import com.happyplaces.domain.usecase.UserUseCase
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class OnboardingViewModel(
    getCurrentAuthUseCase: GetCurrentAuthUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {
    init {
        viewModelScope.launch {
            getCurrentAuthUseCase()
                .filterNotNull()
                .distinctUntilChanged()
                .collect { //Flow<User?>
                    setUser(it)
                }
        }
    }

    data class UiState(
        val id: String = "",
        val email: String = "",
        val accountId: String = "",
        val avatarUri: Uri? = null,
        val username: String = "",
        val bio: String = "",
        val isCheckingAccountId: Boolean = false,
        val accountIdError: String? = null,
    ) {
        val accountIdValid: Boolean
            get() = accountId.length >= 4 && accountIdError == null
        val emailIsValid: Boolean
            get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    var uiState by mutableStateOf(UiState())
        private set

    /**
     * 設定用戶資訊
     * @param user 用戶物件
     */
    fun setUser(user: User) {
        uiState = uiState.copy(
            id = user.id,
            accountId = user.accountID,
            username = user.name,
            avatarUri = user.avatarUrl.toUri(),
            email = user.email
        )
    }

    /**
     * 完成註冊流程
     */
    fun onFinish() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("LinLi", "onFinish: uiState = $uiState")
            val finalAccountId = if (uiState.accountId.startsWith("@")) {
                uiState.accountId
            } else {
                "@${uiState.accountId}"
            }
            
            val result = userUseCase.addUser(
                User(
                    id = uiState.id,
                    name = uiState.username,
                    accountID = finalAccountId,
                    avatarUrl = uiState.avatarUri?.toString() ?: "",
                    email = uiState.email,
                    bio = uiState.bio,
                    createdAt = System.currentTimeMillis(),
                    profileCompleted = true
                )
            ).collect {
                when (it) {
                    is ApiResource.Success -> Log.d("OnFinish", "User added successfully")
                    is ApiResource.Error -> Log.e(
                        "OnFinish",
                        "Error adding user: ${it.message}"
                    )

                    is ApiResource.Loading -> Log.d("OnFinish", "Adding user in progress")
                }
            }
        }
    }

    /**
     * 電子郵件變更事件
     * @param value 新的電子郵件值
     */
    fun onEmailChange(value: String) {
        uiState = uiState.copy(
            email = value
        )
    }

    /**
     * 帳號 ID 變更事件
     * @param value 新的帳號 ID 值
     */
    fun onAccountIdChange(value: String) {
        val clean = value
            .replace("@", "")
            .take(20)
            .trim()
            .filter { it.isLetterOrDigit() || it == '_' }

        uiState = uiState.copy(
            accountId = clean,
            accountIdError = null
        )

        // 如果長度符合要求，檢查帳號是否可用
        if (clean.length >= 4) {
            checkAccountIdAvailability("@$clean")
        }
    }

    /**
     * 檢查帳號 ID 是否可用
     * @param accountId 要檢查的帳號 ID（包含 @ 符號）
     */
    private fun checkAccountIdAvailability(accountId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isCheckingAccountId = true)

            userUseCase.checkAccountIdExists(accountId).collect { result ->
                when (result) {
                    is ApiResource.Success<Boolean> -> {
                        uiState = uiState.copy(
                            isCheckingAccountId = false,
                            accountIdError = if (result.data == true) {
                                "此帳號已被使用"
                            } else null
                        )
                    }

                    is ApiResource.Error<Boolean> -> {
                        uiState = uiState.copy(
                            isCheckingAccountId = false,
                            accountIdError = "檢查帳號時發生錯誤"
                        )
                    }

                    is ApiResource.Loading<Boolean> -> {
                        uiState = uiState.copy(isCheckingAccountId = true)
                    }
                }
            }
        }
    }

    /**
     * 頭像選擇事件
     * @param uri 頭像 URI
     */
    fun onAvatarPicked(uri: Uri) {
        uiState = uiState.copy(avatarUri = uri)
    }

    /**
     * 使用者暱稱變更事件
     * @param value 新的暱稱值
     */
    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value)
    }

    /**
     * 個人簡介變更事件
     * @param value 新的個人簡介值
     */
    fun onBioChange(value: String) {
        uiState = uiState.copy(bio = value)
    }
}
