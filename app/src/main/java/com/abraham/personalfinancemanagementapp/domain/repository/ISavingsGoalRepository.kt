package com.abraham.personalfinancemanagementapp.domain.repository

import com.abraham.personalfinancemanagementapp.data.model.SavingsGoal
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for SavingsGoal operations
 */
interface ISavingsGoalRepository {

    fun getAllSavingsGoals(userId: String): Flow<List<SavingsGoal>>

    suspend fun getSavingsGoalById(id: String): SavingsGoal?

    fun getIncompleteSavingsGoals(userId: String): Flow<List<SavingsGoal>>

    suspend fun addSavingsGoal(savingsGoal: SavingsGoal)

    suspend fun updateSavingsGoal(savingsGoal: SavingsGoal)

    suspend fun deleteSavingsGoal(savingsGoalId: String)
}








