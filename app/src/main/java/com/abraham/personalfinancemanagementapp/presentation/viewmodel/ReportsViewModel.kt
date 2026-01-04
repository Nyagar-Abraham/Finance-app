package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.data.model.Budget
import com.abraham.personalfinancemanagementapp.data.model.Category
import com.abraham.personalfinancemanagementapp.data.model.Transaction
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.IBudgetRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ICategoryRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ITransactionRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for Reports screen
 */
class ReportsViewModel(
    private val authRepository: IAuthRepository,
    private val transactionRepository: ITransactionRepository,
    private val categoryRepository: ICategoryRepository,
    private val budgetRepository: IBudgetRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val selectedPeriod = MutableStateFlow(ReportPeriod.MONTHLY)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val user = authRepository.getCurrentUser()
            if (user != null) {
                // Load transactions
                transactionRepository.getAllTransactions(user.id)
                    .catch { e ->
                        e.printStackTrace()
                        _isLoading.value = false
                    }
                    .collect { transactionList ->
                        _transactions.value = transactionList
                        _isLoading.value = false
                    }
                
                // Load categories
                categoryRepository.getAllCategories(user.id)
                    .catch { e ->
                        e.printStackTrace()
                    }
                    .collect { categoryList ->
                        _categories.value = categoryList
                    }
                
                // Load budgets for current month
                val calendar = Calendar.getInstance()
                val currentMonth = calendar.get(Calendar.MONTH) + 1
                val currentYear = calendar.get(Calendar.YEAR)
                budgetRepository.getBudgetsByMonthYear(user.id, currentMonth, currentYear)
                    .catch { e ->
                        e.printStackTrace()
                    }
                    .collect { budgetList ->
                        _budgets.value = budgetList
                    }
            } else {
                _isLoading.value = false
            }
        }
    }

    val totalIncome: StateFlow<Double> = _transactions
        .map { transactions ->
            getFilteredTransactions(transactions)
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
            getFilteredTransactions(transactions)
                .filter { it.type == Constants.TRANSACTION_TYPE_EXPENSE }
                .sumOf { it.amount }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val netBalance: StateFlow<Double> = combine(totalIncome, totalExpense) { income, expense ->
        income - expense
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val expenseByCategory: StateFlow<Map<String, Double>> = combine(
        _transactions,
        _categories
    ) { transactions, categories ->
        val filteredTransactions = getFilteredTransactions(transactions)
            .filter { it.type == Constants.TRANSACTION_TYPE_EXPENSE }
        
        filteredTransactions
            .groupBy { it.categoryId }
            .mapValues { (categoryId, transactions) -> 
                transactions.sumOf { it.amount }
            }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val incomeByCategory: StateFlow<Map<String, Double>> = combine(
        _transactions,
        _categories
    ) { transactions, categories ->
        val filteredTransactions = getFilteredTransactions(transactions)
            .filter { it.type == Constants.TRANSACTION_TYPE_INCOME }
        
        filteredTransactions
            .groupBy { it.categoryId }
            .mapValues { (categoryId, transactions) -> 
                transactions.sumOf { it.amount }
            }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
    
    // Budget progress data
    val budgetsWithSpending: StateFlow<List<BudgetWithProgress>> = combine(
        _budgets,
        _transactions
    ) { budgets, transactions ->
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)
        
        calendar.set(currentYear, currentMonth - 1, 1, 0, 0, 0)
        val monthStart = calendar.time
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val monthEnd = calendar.time
        
        val monthlyTransactions = transactions.filter { 
            it.date >= monthStart && it.date <= monthEnd 
        }
        
        budgets.map { budget ->
            val spent = monthlyTransactions
                .filter { 
                    it.categoryId == budget.categoryId && 
                    it.type == Constants.TRANSACTION_TYPE_EXPENSE
                }
                .sumOf { it.amount }
            
            BudgetWithProgress(
                budget = budget,
                spent = spent,
                progress = if (budget.amount > 0) (spent / budget.amount).toFloat() else 0f
            )
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val monthlyData: StateFlow<List<MonthlyData>> = _transactions
        .map { transactions ->
            val calendar = Calendar.getInstance()
            val monthlyMap = mutableMapOf<String, MonthlyData>()
            
            transactions.forEach { transaction ->
                calendar.time = transaction.date
                val key = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}"
                
                val existing = monthlyMap[key] ?: MonthlyData(
                    month = calendar.get(Calendar.MONTH) + 1,
                    year = calendar.get(Calendar.YEAR),
                    income = 0.0,
                    expense = 0.0
                )
                
                monthlyMap[key] = when (transaction.type) {
                    Constants.TRANSACTION_TYPE_INCOME -> existing.copy(income = existing.income + transaction.amount)
                    Constants.TRANSACTION_TYPE_EXPENSE -> existing.copy(expense = existing.expense + transaction.amount)
                    else -> existing
                }
            }
            
            monthlyMap.values.sortedWith(compareBy<MonthlyData> { it.year }.thenBy { it.month })
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private fun getFilteredTransactions(transactions: List<Transaction>): List<Transaction> {
        val calendar = Calendar.getInstance()
        val now = Date()
        
        return when (selectedPeriod.value) {
            ReportPeriod.WEEKLY -> {
                calendar.time = now
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val weekAgo = calendar.time
                transactions.filter { it.date >= weekAgo }
            }
            ReportPeriod.MONTHLY -> {
                calendar.time = now
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                val monthStart = calendar.time
                transactions.filter { it.date >= monthStart }
            }
            ReportPeriod.YEARLY -> {
                calendar.time = now
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                val yearStart = calendar.time
                transactions.filter { it.date >= yearStart }
            }
            ReportPeriod.ALL -> transactions
        }
    }

    fun setPeriod(period: ReportPeriod) {
        selectedPeriod.value = period
    }

    fun refresh() {
        loadData()
    }
    
    fun getCategoryName(categoryId: String): String {
        return _categories.value.find { it.id == categoryId }?.name ?: "Uncategorized"
    }
}

enum class ReportPeriod {
    WEEKLY, MONTHLY, YEARLY, ALL
}

data class MonthlyData(
    val month: Int,
    val year: Int,
    val income: Double,
    val expense: Double
) {
    val net: Double
        get() = income - expense
}

data class BudgetWithProgress(
    val budget: Budget,
    val spent: Double,
    val progress: Float
) {
    val remaining: Double
        get() = budget.amount - spent
    
    val isOverBudget: Boolean
        get() = spent > budget.amount
    
    val isWarning: Boolean
        get() = progress >= Constants.BUDGET_WARNING_THRESHOLD && !isOverBudget
}






