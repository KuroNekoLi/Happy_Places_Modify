package com.happyplaces.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyplaces.domain.model.User
import com.happyplaces.domain.usecase.GetCurrentAuthUseCase
import com.happyplaces.domain.usecase.SignOutUseCase
import com.happyplaces.domain.usecase.UserUseCase
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userUseCase: UserUseCase,
    getCurrentAuthUseCase: GetCurrentAuthUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val authUiState: StateFlow<AuthUiState> =
        getCurrentAuthUseCase()                                         // Flow<UserEntity?>
            .flatMapLatest { user ->                                    // 當 user 有變動時，取消舊的 profileCompleted 查詢
                if (user == null) {
                    flowOf(AuthUiState.LoggedOut)
                } else {
                    userUseCase.isUserProfileCompleted(user.id)         // Flow<ApiResource<Boolean>>
                        .map { resource ->
                            when (resource) {
                                is ApiResource.Loading -> AuthUiState.Loading
                                is ApiResource.Success -> AuthUiState.LoggedIn(
                                    User(
                                        id = user.id,
                                        name = user.name,
                                        avatarUrl = user.avatarUrl,
                                        email = user.email,
                                        profileCompleted = resource.data == true,  // 這裡拿到真正的 Boolean
                                        isAnonymous = user.isAnonymous
                                    )
                                )

                                is ApiResource.Error -> AuthUiState.Error(
                                    resource.message ?: "未知錯誤"
                                )
                            }
                        }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = AuthUiState.Loading
            )

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase.invoke()
        }
    }
}

sealed class AuthUiState {
    object Loading : AuthUiState()
    data class LoggedIn(val user: User) : AuthUiState()
    object LoggedOut : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}