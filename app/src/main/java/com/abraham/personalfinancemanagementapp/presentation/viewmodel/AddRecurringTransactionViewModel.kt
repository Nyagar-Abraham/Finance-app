package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.data.model.Category
import com.abraham.personalfinancemanagementapp.data.model.RecurringTransaction
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ICategoryRepository
import com.abraham.personalfinancemanagementapp.domain.repository.IRecurringTransactionRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for Add/Edit Recurring Transaction screen
 */
class AddRecurringTransactionViewModel(
    private val authRepository: IAuthRepository,
    private val recurringTransactionRepository: IRecurringTransactionRepository,
    private val categoryRepository: ICategoryRepository,
    private val recurringTransactionId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddRecurringTransactionUiState>(AddRecurringTransactionUiState.Idle)
    val uiState: StateFlow<AddRecurringTransactionUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val categories = MutableStateFlow<List<Category>>(emptyList())
    val expenseCategories = MutableStateFlow<List<Category>>(emptyList())
    val incomeCategories = MutableStateFlow<List<Category>>(emptyList())

    var selectedType = MutableStateFlow(Constants.TRANSACTION_TYPE_EXPENSE)
    var selectedCategory = MutableStateFlow<Category?>(null)
    var amount = MutableStateFlow("")
    var notes = MutableStateFlow("")
    var paymentMethod = MutableStateFlow(Constants.PAYMENT_METHOD_CASH)
    var frequency = MutableStateFlow(Constants.FREQUENCY_MONTHLY)
    var nextDueDate = MutableStateFlow(Date())
    var isActive = MutableStateFlow(true)

    init {
        loadCategories()
        if (recurringTransactionId != null) {
            loadRecurringTransaction()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                categoryRepository.getAllCategories(user.id)
                    .catch { e ->
                        e.printStackTrace()
                    }
                    .collect { categoryList ->
                        categories.value = categoryList
                        expenseCategories.value = categoryList.filter { 
                            it.type == Constants.TRANSACTION_TYPE_EXPENSE 
                        }
                        incomeCategories.value = categoryList.filter { 
                            it.type == Constants.TRANSACTION_TYPE_INCOME 
                        }
                    }
            }
        }
    }

    private fun loadRecurringTransaction() {
        viewModelScope.launch {
            if (recurringTransactionId != null) {
                _isLoading.value = true
                try {
                    val recurringTransaction = recurringTransactionRepository.getRecurringTransactionById(recurringTransactionId)
                    if (recurringTransaction != null) {
                        selectedType.value = recurringTransaction.type
                        amount.value = recurringTransaction.amount.toString()
                        notes.value = recurringTransaction.notes
                        paymentMethod.value = recurringTransaction.paymentMethod
                        frequency.value = recurringTransaction.frequency
                        nextDueDate.value = recurringTransaction.nextDueDate
                        isActive.value = recurringTransaction.isActive
                        
                        // Find and set category
                        val category = categories.value.find { it.id == recurringTransaction.categoryId }
                        selectedCategory.value = category
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.value = AddRecurringTransactionUiState.Error(e.message ?: "Failed to load recurring transaction")
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun saveRecurringTransaction() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    _uiState.value = AddRecurringTransactionUiState.Error("User not logged in")
                    return@launch
                }

                if (selectedCategory.value == null) {
                    _uiState.value = AddRecurringTransactionUiState.Error("Please select a category")
                    return@launch
                }

                val amountValue = amount.value.toDoubleOrNull() ?: 0.0

                val recurringTransaction = RecurringTransaction(
                    id = recurringTransactionId ?: "",
                    userId = user.id,
                    type = selectedType.value,
                    amount = amountValue,
                    categoryId = selectedCategory.value!!.id,
                    notes = notes.value.trim(),
                    paymentMethod = paymentMethod.value,
                    frequency = frequency.value,
                    nextDueDate = nextDueDate.value,
                    isActive = isActive.value
                )

                if (recurringTransactionId == null) {
                    recurringTransactionRepository.addRecurringTransaction(recurringTransaction)
                } else {
                    recurringTransactionRepository.updateRecurringTransaction(recurringTransaction)
                }

                _uiState.value = AddRecurringTransactionUiState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = AddRecurringTransactionUiState.Error(e.message ?: "Failed to save recurring transaction")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

sealed class AddRecurringTransactionUiState {
    data object Idle : AddRecurringTransactionUiState()
    data object Success : AddRecurringTransactionUiState()
    data class Error(val message: String) : AddRecurringTransactionUiState()
}








