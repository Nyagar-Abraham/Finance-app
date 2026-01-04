package com.abraham.personalfinancemanagementapp.data.repository

import com.abraham.personalfinancemanagementapp.data.local.dao.TransactionDao
import com.abraham.personalfinancemanagementapp.data.mapper.TransactionMapper
import com.abraham.personalfinancemanagementapp.data.model.Transaction
import com.abraham.personalfinancemanagementapp.data.model.SyncStatus
import com.abraham.personalfinancemanagementapp.domain.repository.ITransactionRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Implementation of ITransactionRepository using Room and Firestore
 */
class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val firestore: FirebaseFirestore
) : ITransactionRepository {

    override fun getAllTransactions(userId: String): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions(userId)
            .map { entities -> entities.map { TransactionMapper.toModel(it) } }
    }

    override suspend fun getTransactionById(id: String): Transaction? {
        val entity = transactionDao.getTransactionById(id)
        return entity?.let { TransactionMapper.toModel(it) }
    }

    override fun getTransactionsByType(userId: String, type: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(userId, type)
            .map { entities -> entities.map { TransactionMapper.toModel(it) } }
    }

    override fun getTransactionsByCategory(userId: String, categoryId: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByCategory(userId, categoryId)
            .map { entities -> entities.map { TransactionMapper.toModel(it) } }
    }

    override fun getTransactionsByDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(userId, startDate, endDate)
            .map { entities -> entities.map { TransactionMapper.toModel(it) } }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        val entity = TransactionMapper.toEntity(
            transaction.copy(
                syncStatus = SyncStatus.PENDING,
                updatedAt = java.util.Date()
            )
        )
        transactionDao.insertTransaction(entity)
        
        // Sync to Firestore in background
        syncTransactionToFirestore(transaction)
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        val entity = TransactionMapper.toEntity(
            transaction.copy(
                syncStatus = SyncStatus.PENDING,
                updatedAt = java.util.Date()
            )
        )
        transactionDao.updateTransaction(entity)
        
        // Sync to Firestore in background
        syncTransactionToFirestore(transaction)
    }

    override suspend fun deleteTransaction(transactionId: String) {
        transactionDao.deleteTransactionById(transactionId)
        
        // Delete from Firestore in background
        try {
            val transaction = getTransactionById(transactionId)
            if (transaction != null) {
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(transaction.userId)
                    .collection(Constants.COLLECTION_TRANSACTIONS)
                    .document(transactionId)
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            // Log error but don't fail the operation
            e.printStackTrace()
        }
    }

    override suspend fun syncTransactions(userId: String) {
        // This will be implemented later for full sync functionality
        // For now, just mark pending transactions
    }

    private suspend fun syncTransactionToFirestore(transaction: Transaction) {
        try {
            val transactionMap = mapOf(
                "id" to transaction.id,
                "userId" to transaction.userId,
                "type" to transaction.type,
                "amount" to transaction.amount,
                "categoryId" to transaction.categoryId,
                "date" to com.google.firebase.Timestamp(transaction.date.time / 1000, ((transaction.date.time % 1000) * 1000000).toInt()),
                "notes" to transaction.notes,
                "paymentMethod" to transaction.paymentMethod,
                "tags" to transaction.tags,
                "receiptUrl" to transaction.receiptUrl,
                "createdAt" to com.google.firebase.Timestamp(transaction.createdAt.time / 1000, ((transaction.createdAt.time % 1000) * 1000000).toInt()),
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
            
            firestore.collection(Constants.COLLECTION_USERS)
                .document(transaction.userId)
                .collection(Constants.COLLECTION_TRANSACTIONS)
                .document(transaction.id)
                .set(transactionMap)
                .await()
            
            // Update sync status after successful sync
            val entity = TransactionMapper.toEntity(
                transaction.copy(syncStatus = SyncStatus.SYNCED)
            )
            transactionDao.updateTransaction(entity)
        } catch (e: Exception) {
            // Mark as failed if sync fails
            val entity = TransactionMapper.toEntity(
                transaction.copy(syncStatus = SyncStatus.FAILED)
            )
            transactionDao.updateTransaction(entity)
            e.printStackTrace()
        }
    }
}

