package com.happyplaces.presentation.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyplaces.domain.GetCurrentUserUseCase
import com.happyplaces.domain.UserUseCase
import com.happyplaces.domain.model.AuthUser
import com.happyplaces.domain.model.User
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OnboardingViewModel(
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {
    init {
        viewModelScope.launch {
            getCurrentUserUseCase().collect {
                it?.let { setUser(it) }
            }
        }
    }

    data class UiState(
        val id: String = "",
        val email: String = "",
        val emailValid: Boolean = false,
        val accountId: String = "",
        val avatarUri: Uri? = null,
        val username: String = "",
        val bio: String = "",
    ) {
        val accountIdValid: Boolean
            get() = accountId.length >= 4
    }

    var uiState by mutableStateOf(UiState())
        private set

    fun setUser(authUser: AuthUser) {
//        if (authUser.isAnonymous.not()) {
        uiState = uiState.copy(
            id = authUser.id,
            accountId = authUser.name,
            avatarUri = authUser.image.toUri(),
            email = authUser.email
        )
//        }
    }

    fun onFinish() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("LinLi", "onFinish: uiState = $uiState")
            userUseCase.addUser(
                User(
                    id = uiState.id,
                    name = uiState.username,
                    accountID = uiState.accountId,
                    avatarUrl = uiState.avatarUri?.toString() ?: "",
                    email = uiState.email,
                    bio = uiState.bio,
                    createdAt = System.currentTimeMillis(),
                    profileCompleted = true
                )
            ).collect { result ->
                when (result) {
                    is ApiResource.Success -> Log.d("OnFinish", "User added successfully")
                    is ApiResource.Error -> Log.e(
                        "OnFinish",
                        "Error adding user: ${result.message}"
                    )

                    is ApiResource.Loading -> Log.d("OnFinish", "Adding user in progress")
                }
            }
        }
    }
    // --------- 事件 ----------
    fun onEmailChange(value: String) {
        uiState = uiState.copy(
            email = value,
            emailValid = EMAIL_REGEX.matches(value)
        )
    }

    fun onAccountIdChange(value: String) {
        val clean = value
            .replace("@", "")
            .take(20)
            .trim()
            .filter { it.isLetterOrDigit() || it == '_' }

        uiState = uiState.copy(accountId = clean)
    }

    fun onAvatarPicked(uri: Uri) {
        uiState = uiState.copy(avatarUri = uri)
    }

    fun onBioChange(value: String) {
        uiState = uiState.copy(bio = value)
    }

    companion object {
        // RFC-5322 相容度適中的正規式
        private val EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    }
}
