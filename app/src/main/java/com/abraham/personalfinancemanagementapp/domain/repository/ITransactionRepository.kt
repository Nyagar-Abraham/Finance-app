package com.abraham.personalfinancemanagementapp.domain.repository

import com.abraham.personalfinancemanagementapp.data.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Transaction operations
 */
interface ITransactionRepository {

    fun getAllTransactions(userId: String): Flow<List<Transaction>>

    suspend fun getTransactionById(id: String): Transaction?

    fun getTransactionsByType(userId: String, type: String): Flow<List<Transaction>>

    fun getTransactionsByCategory(userId: String, categoryId: String): Flow<List<Transaction>>

    fun getTransactionsByDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<Transaction>>

    suspend fun addTransaction(transaction: Transaction)

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transactionId: String)

    suspend fun syncTransactions(userId: String)
}
