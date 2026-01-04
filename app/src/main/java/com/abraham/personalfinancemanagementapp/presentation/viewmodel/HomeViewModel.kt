package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.data.model.Transaction
import com.abraham.personalfinancemanagementapp.data.model.User
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ITransactionRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for Home screen
 */
class HomeViewModel(
    private val authRepository: IAuthRepository,
    private val transactionRepository: ITransactionRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Get current user
            val user = authRepository.getCurrentUser()
            _currentUser.value = user
            
            if (user != null) {
                // Load transactions
                transactionRepository.getAllTransactions(user.id)
                    .catch { e ->
                        e.printStackTrace()
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

    val totalBalance: StateFlow<Double> = combine(
        _transactions,
        _currentUser
    ) { transactions, user ->
        if (user == null) 0.0
        else {
            val income = transactions
                .filter { it.type == Constants.TRANSACTION_TYPE_INCOME }
                .sumOf { it.amount }
            val expense = transactions
                .filter { it.type == Constants.TRANSACTION_TYPE_EXPENSE }
                .sumOf { it.amount }
            income - expense
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val totalIncome: StateFlow<Double> = _transactions
        .map { transactions ->
            transactions
                .filter { it.type == Constants.TRANSACTION_TYPE_INCOME }
                .sumOf { it.amount }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val totalExpense: StateFlow<Double> = _transactions
        .map { transactions ->
            transactions
                .filter { it.type == Constants.TRANSACTION_TYPE_EXPENSE }
                .sumOf { it.amount }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val recentTransactions: StateFlow<List<Transaction>> = _transactions
        .map { transactions ->
            transactions
                .sortedByDescending { it.date }
                .take(5)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun refresh() {
        loadUserData()
    }
}














