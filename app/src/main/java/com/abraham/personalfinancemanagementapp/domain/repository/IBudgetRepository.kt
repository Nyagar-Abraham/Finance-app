package com.abraham.personalfinancemanagementapp.domain.repository

import com.abraham.personalfinancemanagementapp.data.model.Budget
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Budget operations
 */
interface IBudgetRepository {

    fun getAllBudgets(userId: String): Flow<List<Budget>>

    suspend fun getBudgetById(id: String): Budget?

    fun getBudgetsByMonthYear(userId: String, month: Int, year: Int): Flow<List<Budget>>

    suspend fun getBudgetByCategoryMonthYear(
        userId: String,
        categoryId: String,
        month: Int,
        year: Int
    ): Budget?

    suspend fun addBudget(budget: Budget)

    suspend fun updateBudget(budget: Budget)

    suspend fun deleteBudget(budgetId: String)
}
