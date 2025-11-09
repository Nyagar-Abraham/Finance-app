package com.abraham.personalfinancemanagementapp.data.local.dao

import androidx.room.*
import com.abraham.personalfinancemanagementapp.data.local.entities.BudgetEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Budget entity
 */
@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE userId = :userId")
    fun getAllBudgets(userId: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getBudgetById(id: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE userId = :userId AND month = :month AND year = :year")
    fun getBudgetsByMonthYear(userId: String, month: Int, year: Int): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE userId = :userId AND categoryId = :categoryId AND month = :month AND year = :year")
    suspend fun getBudgetByCategoryMonthYear(
        userId: String,
        categoryId: String,
        month: Int,
        year: Int
    ): BudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteBudgetById(id: String)
}
