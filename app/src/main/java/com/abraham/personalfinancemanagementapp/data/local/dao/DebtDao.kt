package com.abraham.personalfinancemanagementapp.data.local.dao

import androidx.room.*
import com.abraham.personalfinancemanagementapp.data.local.entities.DebtEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Debt entity
 */
@Dao
interface DebtDao {

    @Query("SELECT * FROM debts WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllDebts(userId: String): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts WHERE id = :id")
    suspend fun getDebtById(id: String): DebtEntity?

    @Query("SELECT * FROM debts WHERE userId = :userId AND isPaid = 0")
    fun getUnpaidDebts(userId: String): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts WHERE userId = :userId AND type = :type")
    fun getDebtsByType(userId: String, type: String): Flow<List<DebtEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity)

    @Update
    suspend fun updateDebt(debt: DebtEntity)

    @Delete
    suspend fun deleteDebt(debt: DebtEntity)

    @Query("DELETE FROM debts WHERE id = :id")
    suspend fun deleteDebtById(id: String)
}
