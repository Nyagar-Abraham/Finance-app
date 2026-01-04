package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.data.model.Category
import com.abraham.personalfinancemanagementapp.data.model.Transaction
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ICategoryRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ITransactionRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for Add/Edit Transaction screen
 */
class AddTransactionViewModel(
    private val authRepository: IAuthRepository,
    private val transactionRepository: ITransactionRepository,
    private val categoryRepository: ICategoryRepository,
    private val transactionId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddTransactionUiState>(AddTransactionUiState.Idle)
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

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
    var date = MutableStateFlow(Date())
    var receiptUrl = MutableStateFlow("")

    init {
        loadCategories()
        if (transactionId != null) {
            loadTransaction()
        } else {
            // Initialize default categories for new user
            viewModelScope.launch {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    categoryRepository.initializeDefaultCategories(user.id)
                }
            }
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

    private fun loadTransaction() {
        viewModelScope.launch {
            if (transactionId != null) {
                _isLoading.value = true
                val transaction = transactionRepository.getTransactionById(transactionId)
                if (transaction != null) {
                    selectedType.value = transaction.type
                    amount.value = transaction.amount.toString()
                    notes.value = transaction.notes
                    paymentMethod.value = transaction.paymentMethod
                    date.value = transaction.date
                    
                    // Load category
                    val category = categoryRepository.getCategoryById(transaction.categoryId)
                    selectedCategory.value = category
                    receiptUrl.value = transaction.receiptUrl
                }
                _isLoading.value = false
            }
        }
    }
    
    fun setReceiptUrl(url: String) {
        receiptUrl.value = url
    }

    fun saveTransaction() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user == null) {
                _uiState.value = AddTransactionUiState.Error("User not logged in")
                return@launch
            }

            val category = selectedCategory.value
            if (category == null) {
                _uiState.value = AddTransactionUiState.Error("Please select a category")
                return@launch
            }

            val amountValue = amount.value.toDoubleOrNull()
            if (amountValue == null || amountValue <= 0) {
                _uiState.value = AddTransactionUiState.Error("Please enter a valid amount")
                return@launch
            }

            _isLoading.value = true
            _uiState.value = AddTransactionUiState.Loading

            val transaction = Transaction(
                id = transactionId ?: UUID.randomUUID().toString(),
                userId = user.id,
                type = selectedType.value,
                amount = amountValue,
                categoryId = category.id,
                date = date.value,
                notes = notes.value,
                paymentMethod = paymentMethod.value,
                tags = emptyList(),
                receiptUrl = receiptUrl.value,
                syncStatus = com.abraham.personalfinancemanagementapp.data.model.SyncStatus.PENDING,
                createdAt = if (transactionId != null) Date() else Date(),
                updatedAt = Date()
            )

            try {
                if (transactionId != null) {
                    transactionRepository.updateTransaction(transaction)
                } else {
                    transactionRepository.addTransaction(transaction)
                }
                _uiState.value = AddTransactionUiState.Success
                _isLoading.value = false
            } catch (e: Exception) {
                _uiState.value = AddTransactionUiState.Error(e.message ?: "Failed to save transaction")
                _isLoading.value = false
            }
        }
    }

    fun onTypeChanged(type: String) {
        selectedType.value = type
        selectedCategory.value = null // Reset category when type changes
    }
}

sealed class AddTransactionUiState {
    data object Idle : AddTransactionUiState()
    data object Loading : AddTransactionUiState()
    data object Success : AddTransactionUiState()
    data class Error(val message: String) : AddTransactionUiState()
}









