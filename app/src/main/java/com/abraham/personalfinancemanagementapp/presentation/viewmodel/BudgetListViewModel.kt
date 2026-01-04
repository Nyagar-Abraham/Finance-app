package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.data.model.Budget
import com.abraham.personalfinancemanagementapp.data.model.Transaction
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.IBudgetRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ITransactionRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for Budget List screen
 */
class BudgetListViewModel(
    private val authRepository: IAuthRepository,
    private val budgetRepository: IBudgetRepository,
    private val transactionRepository: ITransactionRepository
) : ViewModel() {

    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH) + 1)
    val selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val user = authRepository.getCurrentUser()
            if (user != null) {
                // Load budgets for current month/year
                budgetRepository.getBudgetsByMonthYear(user.id, selectedMonth.value, selectedYear.value)
                    .catch { e ->
                        e.printStackTrace()
                        _isLoading.value = false
                    }
                    .collect { budgetList ->
                        _budgets.value = budgetList
                        _isLoading.value = false
                    }
                
                // Load transactions for current month/year
                val calendar = Calendar.getInstance()
                calendar.set(selectedYear.value, selectedMonth.value - 1, 1, 0, 0, 0)
                val monthStart = calendar.time
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                val monthEnd = calendar.time
                
                transactionRepository.getTransactionsByDateRange(
                    user.id,
                    monthStart.time,
                    monthEnd.time
                )
                    .catch { e ->
                        e.printStackTrace()
                    }
                    .collect { transactionList ->
                        _transactions.value = transactionList
                    }
            } else {
                _isLoading.value = false
            }
        }
    }

    val budgetsWithSpending: StateFlow<List<BudgetWithSpending>> = combine(
        _budgets,
        _transactions
    ) { budgets, transactions ->
        budgets.map { budget ->
            val spending = transactions
                .filter { 
                    it.categoryId == budget.categoryId && 
                    it.type == Constants.TRANSACTION_TYPE_EXPENSE
                }
                .sumOf { it.amount }
            
            BudgetWithSpending(
                budget = budget,
                spent = spending,
                remaining = budget.amount - spending,
                percentage = if (budget.amount > 0) (spending / budget.amount).toFloat() else 0f
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setMonthYear(month: Int, year: Int) {
        selectedMonth.value = month
        selectedYear.value = year
        loadData()
    }

    fun refresh() {
        loadData()
    }
    
    // Note: Budget notifications should be triggered from the UI layer where context is available
    // This method can be called from the screen composable
}

data class BudgetWithSpending(
    val budget: Budget,
    val spent: Double,
    val remaining: Double,
    val percentage: Float
) {
    val isOverBudget: Boolean
        get() = spent > budget.amount
    
    val isWarning: Boolean
        get() = percentage >= Constants.BUDGET_WARNING_THRESHOLD && !isOverBudget
}







