package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.data.model.Transaction
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Transaction List screen
 */
class TransactionListViewModel(
    private val authRepository: IAuthRepository,
    private val transactionRepository: ITransactionRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val user = authRepository.getCurrentUser()
            if (user != null) {
                transactionRepository.getAllTransactions(user.id)
                    .catch { e ->
                        e.printStackTrace()
                        _isLoading.value = false
                    }
                    .collect { transactionList ->
                        _transactions.value = transactionList
                        _isLoading.value = false
                    }
            } else {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadTransactions()
    }
}














