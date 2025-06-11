package com.happyplaces.presentation.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyplaces.domain.model.User
import com.happyplaces.domain.usecase.GetCurrentUserUseCase
import com.happyplaces.domain.usecase.UserUseCase
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 編輯個人資料 ViewModel
 */
class EditProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {

    data class UiState(
        val id: String = "",
        val username: String = "",
        val bio: String = "",
        val accountId: String = "",
        val avatarUrl: String = "",
        val isLoading: Boolean = false,
        val event: EditProfileEvent? = null
    )

    sealed class EditProfileEvent {
        data object ShowImagePicker : EditProfileEvent()
        data class ShowMessage(val message: String) : EditProfileEvent()
        data object NavigateBack : EditProfileEvent()
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * 載入當前用戶資料
     */
    fun loadCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { result ->
                when (result) {
                    is ApiResource.Success -> {
                        result.data?.let { user ->
                            _uiState.value = _uiState.value.copy(
                                id = user.id,
                                username = user.name,
                                bio = user.bio,
                                accountId = user.accountID,
                                avatarUrl = user.avatarUrl,
                                isLoading = false
                            )
                        }
                    }

                    is ApiResource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            event = EditProfileEvent.ShowMessage("載入用戶資料失敗: ${result.message}")
                        )
                    }

                    is ApiResource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    /**
     * 暱稱變更事件
     * @param username 新的暱稱
     */
    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    /**
     * 個人簡介變更事件
     * @param bio 新的個人簡介
     */
    fun onBioChange(bio: String) {
        _uiState.value = _uiState.value.copy(bio = bio)
    }

    /**
     * 頭像點擊事件
     */
    fun onAvatarClick() {
        _uiState.value = _uiState.value.copy(event = EditProfileEvent.ShowImagePicker)
    }

    /**
     * 頭像選擇事件
     * @param uri 選擇的圖片 URI
     */
    fun onAvatarSelected(uri: Uri) {
        _uiState.value = _uiState.value.copy(avatarUrl = uri.toString())
    }

    /**
     * 儲存變更事件
     */
    fun onSaveClick() {
        val state = _uiState.value

        if (state.username.isBlank()) {
            _uiState.value = state.copy(event = EditProfileEvent.ShowMessage("暱稱不能為空"))
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)

            val updatedUser = User(
                id = state.id,
                name = state.username,
                accountID = state.accountId,
                avatarUrl = state.avatarUrl,
                email = "", // 保持原有值，這裡暫時留空
                bio = state.bio,
                createdAt = System.currentTimeMillis(), // 這應該保持原有值
                profileCompleted = true
            )

            userUseCase.updateUser(updatedUser).collect { result ->
                when (result) {
                    is ApiResource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            event = EditProfileEvent.ShowMessage("個人資料更新成功")
                        )
                        // 延遲導航，讓用戶看到成功訊息
                        kotlinx.coroutines.delay(1000)
                        _uiState.value = _uiState.value.copy(event = EditProfileEvent.NavigateBack)
                    }

                    is ApiResource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            event = EditProfileEvent.ShowMessage("更新失敗: ${result.message}")
                        )
                    }

                    is ApiResource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    /**
     * 事件已消費
     */
    fun onEventConsumed() {
        _uiState.value = _uiState.value.copy(event = null)
    }
}