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
import com.happyplaces.domain.model.User
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OnboardingViewModel(
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {
    init {
        viewModelScope.launch {
            getCurrentUserUseCase()
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
    ) {
        val accountIdValid: Boolean
            get() = accountId.length in 4..20
        val emailIsValid: Boolean
            get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    var uiState by mutableStateOf(UiState())
        private set

    fun setUser(user: User) {
        uiState = uiState.copy(
            id = user.id,
            accountId = user.accountID,
            username = user.name,
            avatarUri = user.avatarUrl.toUri(),
            email = user.email
        )
    }

    fun onFinish() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("LinLi", "onFinish: uiState = $uiState")
            val result = userUseCase.addUser(
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
            ).first()
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

    // --------- 事件 ----------
    fun onEmailChange(value: String) {
        uiState = uiState.copy(
            email = value
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
}
