package com.abraham.personalfinancemanagementapp.domain.repository

import com.abraham.personalfinancemanagementapp.data.model.RecurringTransaction
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for RecurringTransaction operations
 */
interface IRecurringTransactionRepository {

    fun getAllRecurringTransactions(userId: String): Flow<List<RecurringTransaction>>

    suspend fun getRecurringTransactionById(id: String): RecurringTransaction?

    fun getActiveRecurringTransactions(userId: String): Flow<List<RecurringTransaction>>

    suspend fun addRecurringTransaction(recurringTransaction: RecurringTransaction)

    suspend fun updateRecurringTransaction(recurringTransaction: RecurringTransaction)

    suspend fun deleteRecurringTransaction(recurringTransactionId: String)
}








