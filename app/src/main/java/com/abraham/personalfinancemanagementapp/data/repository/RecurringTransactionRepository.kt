package com.abraham.personalfinancemanagementapp.data.repository

import com.abraham.personalfinancemanagementapp.data.local.dao.RecurringTransactionDao
import com.abraham.personalfinancemanagementapp.data.mapper.RecurringTransactionMapper
import com.abraham.personalfinancemanagementapp.data.model.RecurringTransaction
import com.abraham.personalfinancemanagementapp.domain.repository.IRecurringTransactionRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Implementation of IRecurringTransactionRepository using Room and Firestore
 */
class RecurringTransactionRepository(
    private val recurringTransactionDao: RecurringTransactionDao,
    private val firestore: FirebaseFirestore
) : IRecurringTransactionRepository {

    override fun getAllRecurringTransactions(userId: String): Flow<List<RecurringTransaction>> {
        return recurringTransactionDao.getAllRecurringTransactions(userId)
            .map { entities -> entities.map { RecurringTransactionMapper.toModel(it) } }
    }

    override suspend fun getRecurringTransactionById(id: String): RecurringTransaction? {
        val entity = recurringTransactionDao.getRecurringTransactionById(id)
        return entity?.let { RecurringTransactionMapper.toModel(it) }
    }

    override fun getActiveRecurringTransactions(userId: String): Flow<List<RecurringTransaction>> {
        return recurringTransactionDao.getActiveRecurringTransactions(userId)
            .map { entities -> entities.map { RecurringTransactionMapper.toModel(it) } }
    }

    override suspend fun addRecurringTransaction(recurringTransaction: RecurringTransaction) {
        val recurringTransactionWithId = if (recurringTransaction.id.isEmpty()) {
            recurringTransaction.copy(id = UUID.randomUUID().toString())
        } else {
            recurringTransaction
        }
        val entity = RecurringTransactionMapper.toEntity(recurringTransactionWithId)
        recurringTransactionDao.insertRecurringTransaction(entity)
        
        // Sync to Firestore
        syncRecurringTransactionToFirestore(recurringTransactionWithId)
    }

    override suspend fun updateRecurringTransaction(recurringTransaction: RecurringTransaction) {
        val entity = RecurringTransactionMapper.toEntity(recurringTransaction)
        recurringTransactionDao.updateRecurringTransaction(entity)
        
        // Sync to Firestore
        syncRecurringTransactionToFirestore(recurringTransaction)
    }

    override suspend fun deleteRecurringTransaction(recurringTransactionId: String) {
        recurringTransactionDao.deleteRecurringTransactionById(recurringTransactionId)
        
        // Delete from Firestore
        try {
            val recurringTransaction = getRecurringTransactionById(recurringTransactionId)
            if (recurringTransaction != null) {
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(recurringTransaction.userId)
                    .collection(Constants.COLLECTION_RECURRING_TRANSACTIONS)
                    .document(recurringTransactionId)
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun syncRecurringTransactionToFirestore(recurringTransaction: RecurringTransaction) {
        try {
            val recurringTransactionMap = mapOf(
                "id" to recurringTransaction.id,
                "userId" to recurringTransaction.userId,
                "type" to recurringTransaction.type,
                "amount" to recurringTransaction.amount,
                "categoryId" to recurringTransaction.categoryId,
                "notes" to recurringTransaction.notes,
                "paymentMethod" to recurringTransaction.paymentMethod,
                "frequency" to recurringTransaction.frequency,
                "nextDueDate" to com.google.firebase.Timestamp(recurringTransaction.nextDueDate.time / 1000, ((recurringTransaction.nextDueDate.time % 1000) * 1000000).toInt()),
                "isActive" to recurringTransaction.isActive,
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
            
            firestore.collection(Constants.COLLECTION_USERS)
                .document(recurringTransaction.userId)
                .collection(Constants.COLLECTION_RECURRING_TRANSACTIONS)
                .document(recurringTransaction.id)
                .set(recurringTransactionMap)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

