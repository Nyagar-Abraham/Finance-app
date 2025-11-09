package com.abraham.personalfinancemanagementapp.data.local.dao

import androidx.room.*
import com.abraham.personalfinancemanagementapp.data.local.entities.RecurringTransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for RecurringTransaction entity
 */
@Dao
interface RecurringTransactionDao {

    @Query("SELECT * FROM recurring_transactions WHERE userId = :userId")
    fun getAllRecurringTransactions(userId: String): Flow<List<RecurringTransactionEntity>>

    @Query("SELECT * FROM recurring_transactions WHERE id = :id")
    suspend fun getRecurringTransactionById(id: String): RecurringTransactionEntity?

    @Query("SELECT * FROM recurring_transactions WHERE userId = :userId AND isActive = 1")
    fun getActiveRecurringTransactions(userId: String): Flow<List<RecurringTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringTransaction(transaction: RecurringTransactionEntity)

    @Update
    suspend fun updateRecurringTransaction(transaction: RecurringTransactionEntity)

    @Delete
    suspend fun deleteRecurringTransaction(transaction: RecurringTransactionEntity)

    @Query("DELETE FROM recurring_transactions WHERE id = :id")
    suspend fun deleteRecurringTransactionById(id: String)
}
