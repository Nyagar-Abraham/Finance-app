package com.abraham.personalfinancemanagementapp.data.local.dao

import androidx.room.*
import com.abraham.personalfinancemanagementapp.data.local.entities.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for SavingsGoal entity
 */
@Dao
interface SavingsGoalDao {

    @Query("SELECT * FROM savings_goals WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllSavingsGoals(userId: String): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM savings_goals WHERE id = :id")
    suspend fun getSavingsGoalById(id: String): SavingsGoalEntity?

    @Query("SELECT * FROM savings_goals WHERE userId = :userId AND currentAmount < targetAmount")
    fun getIncompleteSavingsGoals(userId: String): Flow<List<SavingsGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoal(savingsGoal: SavingsGoalEntity)

    @Update
    suspend fun updateSavingsGoal(savingsGoal: SavingsGoalEntity)

    @Delete
    suspend fun deleteSavingsGoal(savingsGoal: SavingsGoalEntity)

    @Query("DELETE FROM savings_goals WHERE id = :id")
    suspend fun deleteSavingsGoalById(id: String)
}
