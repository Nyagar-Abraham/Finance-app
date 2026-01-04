package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.data.model.User
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for authentication operations
 */
class AuthViewModel(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _uiState.value = AuthUiState.Loading
            
            authRepository.signInWithEmail(email, password)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                    _isLoading.value = false
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Sign in failed")
                    _isLoading.value = false
                }
        }
    }

    fun signUpWithEmail(email: String, password: String, name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _uiState.value = AuthUiState.Loading
            
            authRepository.signUpWithEmail(email, password, name)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                    _isLoading.value = false
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Sign up failed")
                    _isLoading.value = false
                }
        }
    }

    fun signInWithGoogleIdToken(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _uiState.value = AuthUiState.Loading
            
            if (authRepository is com.abraham.personalfinancemanagementapp.data.repository.AuthRepository) {
                authRepository.signInWithGoogleIdToken(idToken)
                    .onSuccess { user ->
                        _uiState.value = AuthUiState.Success(user)
                        _isLoading.value = false
                    }
                    .onFailure { error ->
                        _uiState.value = AuthUiState.Error(error.message ?: "Google sign in failed")
                        _isLoading.value = false
                    }
            } else {
                _uiState.value = AuthUiState.Error("Google sign in not available")
                _isLoading.value = false
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            authRepository.resetPassword(email)
                .onSuccess {
                    _uiState.value = AuthUiState.PasswordResetSent
                    _isLoading.value = false
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Password reset failed")
                    _isLoading.value = false
                }
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}

/**
 * UI State for authentication
 */
sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    data object PasswordResetSent : AuthUiState()
}

