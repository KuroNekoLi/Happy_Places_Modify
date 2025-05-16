package com.happyplaces.presentation.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class OnboardingViewModel : ViewModel() {

    data class UiState(
        val email: String = "",
        val emailValid: Boolean = false,
        val username: String = "",
        val avatarUri: Uri? = null,
        val bio: String = "",
    ) {
        val usernameValid: Boolean
            get() = username.length >= 4
    }

    var uiState by mutableStateOf(UiState())
        private set

    // --------- 事件 ----------
    fun onEmailChange(value: String) {
        uiState = uiState.copy(
            email = value,
            emailValid = EMAIL_REGEX.matches(value)
        )
    }

    fun onUsernameChange(value: String) {
        val clean = value
            .replace("@", "")
            .take(20)
            .trim()
            .filter { it.isLetterOrDigit() || it == '_' }

        uiState = uiState.copy(username = clean)
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
