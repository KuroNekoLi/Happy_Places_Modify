package com.happyplaces.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyplaces.domain.GetCurrentUserUseCase
import com.happyplaces.domain.UserUseCase
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userUseCase: UserUseCase,
    val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _isRegistered: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isRegistered: StateFlow<Boolean?> get() = _isRegistered
    private val _showToast: MutableStateFlow<String> = MutableStateFlow("")
    val showToast: StateFlow<String> get() = _showToast

    init {
        viewModelScope.launch {
            getCurrentUserUseCase().collect {
                it?.let { checkUserProfileCompleted(it.id) }
            }
        }
        getCurrentUserUseCase().map { authUser ->

        }
    }

    fun checkUserProfileCompleted(id: String) {
        viewModelScope.launch {
            val result = userUseCase.isUserProfileCompleted(id)
            result.collect {
                when (it) {
                    is ApiResource.Success -> {
                        _isRegistered.value = (it.data == true)
                    }

                    is ApiResource.Error -> {
                        _isRegistered.value = false
                        _showToast.value = it.message ?: "Login failed, Please try again later."
                    }

                    is ApiResource.Loading -> {}
                }
            }
        }
    }
}