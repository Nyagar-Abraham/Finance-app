package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ICategoryRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ITransactionRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for Analytics screen
 */
class AnalyticsViewModel(
    private val authRepository: IAuthRepository,
    private val transactionRepository: ITransactionRepository,
    private val categoryRepository: ICategoryRepository
) : ViewModel() {

    private val _analyticsData = MutableStateFlow(AnalyticsData())
    val analyticsData: StateFlow<AnalyticsData> = _analyticsData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadAnalytics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    // Get all transactions
                    val transactions = transactionRepository.getAllTransactions(user.id).first()
                    
                    // Calculate totals
                    val totalIncome = transactions
                        .filter { it.type == Constants.TRANSACTION_TYPE_INCOME }
                        .sumOf { it.amount }
                    
                    val totalExpense = transactions
                        .filter { it.type == Constants.TRANSACTION_TYPE_EXPENSE }
                        .sumOf { it.amount }
                    
                    // Get top expense categories
                    val expenseByCategory = transactions
                        .filter { it.type == Constants.TRANSACTION_TYPE_EXPENSE }
                        .groupBy { it.categoryId }
                        .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
                    
                    val topExpenseCategories = expenseByCategory
                        .mapNotNull { (categoryId, amount) ->
                            val category = categoryRepository.getCategoryById(categoryId)
                            val categoryName = category?.name
                            if (categoryName != null) {
                                categoryName to amount
                            } else {
                                null
                            }
                        }
                        .sortedByDescending { it.second }
                        .take(5)
                        .associate { it }
                    
                    _analyticsData.value = AnalyticsData(
                        totalIncome = totalIncome,
                        totalExpense = totalExpense,
                        netBalance = totalIncome - totalExpense,
                        topExpenseCategories = topExpenseCategories
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

data class AnalyticsData(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netBalance: Double = 0.0,
    val topExpenseCategories: Map<String, Double> = emptyMap()
)

