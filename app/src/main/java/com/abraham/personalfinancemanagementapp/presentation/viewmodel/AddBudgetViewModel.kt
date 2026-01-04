package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.data.model.Budget
import com.abraham.personalfinancemanagementapp.data.model.Category
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.IBudgetRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ICategoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.UUID

/**
 * ViewModel for Add/Edit Budget screen
 */
class AddBudgetViewModel(
    private val authRepository: IAuthRepository,
    private val budgetRepository: IBudgetRepository,
    private val categoryRepository: ICategoryRepository,
    private val budgetId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddBudgetUiState>(AddBudgetUiState.Idle)
    val uiState: StateFlow<AddBudgetUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val categories = MutableStateFlow<List<Category>>(emptyList())
    val expenseCategories = MutableStateFlow<List<Category>>(emptyList())

    var selectedCategory = MutableStateFlow<Category?>(null)
    var amount = MutableStateFlow("")
    var selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH) + 1)
    var selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))

    init {
        loadCategories()
        if (budgetId != null) {
            loadBudget()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                categoryRepository.getCategoriesByType(user.id, "expense")
                    .catch { e ->
                        e.printStackTrace()
                    }
                    .collect { categoryList ->
                        categories.value = categoryList
                        expenseCategories.value = categoryList
                    }
            }
        }
    }

    private fun loadBudget() {
        viewModelScope.launch {
            if (budgetId != null) {
                _isLoading.value = true
                try {
                    val budget = budgetRepository.getBudgetById(budgetId)
                    if (budget != null) {
                        amount.value = budget.amount.toString()
                        selectedMonth.value = budget.month
                        selectedYear.value = budget.year
                        
                        // Find and set category
                        val category = categories.value.find { it.id == budget.categoryId }
                        selectedCategory.value = category
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.value = AddBudgetUiState.Error(e.message ?: "Failed to load budget")
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun saveBudget() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    _uiState.value = AddBudgetUiState.Error("User not logged in")
                    return@launch
                }

                if (selectedCategory.value == null) {
                    _uiState.value = AddBudgetUiState.Error("Please select a category")
                    return@launch
                }

                val amountValue = amount.value.toDoubleOrNull() ?: 0.0
                if (amountValue <= 0) {
                    _uiState.value = AddBudgetUiState.Error("Amount must be greater than 0")
                    return@launch
                }

                // Check if budget already exists for this category/month/year
                val existingBudget = budgetRepository.getBudgetByCategoryMonthYear(
                    user.id,
                    selectedCategory.value!!.id,
                    selectedMonth.value,
                    selectedYear.value
                )

                val budget = Budget(
                    id = budgetId ?: UUID.randomUUID().toString(),
                    userId = user.id,
                    categoryId = selectedCategory.value!!.id,
                    amount = amountValue,
                    month = selectedMonth.value,
                    year = selectedYear.value,
                    spent = existingBudget?.spent ?: 0.0
                )

                if (budgetId == null) {
                    budgetRepository.addBudget(budget)
                } else {
                    budgetRepository.updateBudget(budget)
                }

                _uiState.value = AddBudgetUiState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = AddBudgetUiState.Error(e.message ?: "Failed to save budget")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

sealed class AddBudgetUiState {
    data object Idle : AddBudgetUiState()
    data object Success : AddBudgetUiState()
    data class Error(val message: String) : AddBudgetUiState()
}

