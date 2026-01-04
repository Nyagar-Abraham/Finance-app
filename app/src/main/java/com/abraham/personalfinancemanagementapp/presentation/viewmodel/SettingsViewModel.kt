package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.data.local.PreferencesManager
import com.abraham.personalfinancemanagementapp.data.model.Transaction
import com.abraham.personalfinancemanagementapp.data.model.User
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Settings screen
 */
class SettingsViewModel(
    private val authRepository: IAuthRepository,
    private val transactionRepository: ITransactionRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    val themeMode: StateFlow<String> = preferencesManager.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "system"
    )

    val biometricEnabled: StateFlow<Boolean> = preferencesManager.biometricEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val currency: StateFlow<String> = preferencesManager.currency.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "KSH"
    )

    init {
        loadUser()
        loadTransactions()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUser()
            _currentUser.value = user
            _isLoading.value = false
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                transactionRepository.getAllTransactions(user.id)
                    .catch { e ->
                        e.printStackTrace()
                    }
                    .collect { transactionList ->
                        _transactions.value = transactionList
                    }
            }
        }
    }

    suspend fun signOut(): Result<Unit> {
        return authRepository.signOut()
    }

    fun refresh() {
        loadUser()
        loadTransactions()
    }

    suspend fun setThemeMode(mode: String) {
        preferencesManager.setThemeMode(mode)
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        preferencesManager.setBiometricEnabled(enabled)
    }

    suspend fun setCurrency(currency: String) {
        preferencesManager.setCurrency(currency)
    }
}







