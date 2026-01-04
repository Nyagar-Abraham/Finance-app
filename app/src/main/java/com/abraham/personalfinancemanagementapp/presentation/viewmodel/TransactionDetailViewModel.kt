package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.data.model.Transaction
import com.abraham.personalfinancemanagementapp.domain.repository.ICategoryRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Transaction Detail screen
 */
class TransactionDetailViewModel(
    private val transactionRepository: ITransactionRepository,
    private val categoryRepository: ICategoryRepository
) : ViewModel() {

    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction.asStateFlow()

    private val _categoryName = MutableStateFlow<String?>(null)
    val categoryName: StateFlow<String?> = _categoryName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadTransaction(transactionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val transaction = transactionRepository.getTransactionById(transactionId)
                _transaction.value = transaction
                
                // Load category name
                if (transaction != null) {
                    val category = categoryRepository.getCategoryById(transaction.categoryId)
                    _categoryName.value = category?.name
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            val transaction = _transaction.value
            if (transaction != null) {
                try {
                    transactionRepository.deleteTransaction(transaction.id)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}








