package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.IRecurringTransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Recurring Transaction List screen
 */
class RecurringTransactionListViewModel(
    private val authRepository: IAuthRepository,
    private val recurringTransactionRepository: IRecurringTransactionRepository
) : ViewModel() {

    private val _recurringTransactions = MutableStateFlow<List<com.abraham.personalfinancemanagementapp.data.model.RecurringTransaction>>(emptyList())
    val recurringTransactions: StateFlow<List<com.abraham.personalfinancemanagementapp.data.model.RecurringTransaction>> = _recurringTransactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val showActiveOnly = MutableStateFlow(true)

    init {
        loadRecurringTransactions()
    }

    private fun loadRecurringTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val user = authRepository.getCurrentUser()
            if (user != null) {
                if (showActiveOnly.value) {
                    recurringTransactionRepository.getActiveRecurringTransactions(user.id)
                        .catch { e ->
                            e.printStackTrace()
                            _isLoading.value = false
                        }
                        .collect { recurringTransactionList ->
                            _recurringTransactions.value = recurringTransactionList
                            _isLoading.value = false
                        }
                } else {
                    recurringTransactionRepository.getAllRecurringTransactions(user.id)
                        .catch { e ->
                            e.printStackTrace()
                            _isLoading.value = false
                        }
                        .collect { recurringTransactionList ->
                            _recurringTransactions.value = recurringTransactionList
                            _isLoading.value = false
                        }
                }
            } else {
                _isLoading.value = false
            }
        }
    }

    fun toggleFilter() {
        showActiveOnly.value = !showActiveOnly.value
        loadRecurringTransactions()
    }

    fun refresh() {
        loadRecurringTransactions()
    }

    fun deleteRecurringTransaction(recurringTransactionId: String) {
        viewModelScope.launch {
            try {
                recurringTransactionRepository.deleteRecurringTransaction(recurringTransactionId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}








