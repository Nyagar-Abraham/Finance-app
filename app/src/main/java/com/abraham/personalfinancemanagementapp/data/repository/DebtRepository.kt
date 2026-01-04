package com.abraham.personalfinancemanagementapp.data.repository

import com.abraham.personalfinancemanagementapp.data.local.dao.DebtDao
import com.abraham.personalfinancemanagementapp.data.mapper.DebtMapper
import com.abraham.personalfinancemanagementapp.data.model.Debt
import com.abraham.personalfinancemanagementapp.domain.repository.IDebtRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Implementation of IDebtRepository using Room and Firestore
 */
class DebtRepository(
    private val debtDao: DebtDao,
    private val firestore: FirebaseFirestore
) : IDebtRepository {

    override fun getAllDebts(userId: String): Flow<List<Debt>> {
        return debtDao.getAllDebts(userId)
            .map { entities -> entities.map { DebtMapper.toModel(it) } }
    }

    override suspend fun getDebtById(id: String): Debt? {
        val entity = debtDao.getDebtById(id)
        return entity?.let { DebtMapper.toModel(it) }
    }

    override fun getUnpaidDebts(userId: String): Flow<List<Debt>> {
        return debtDao.getUnpaidDebts(userId)
            .map { entities -> entities.map { DebtMapper.toModel(it) } }
    }

    override fun getDebtsByType(userId: String, type: String): Flow<List<Debt>> {
        return debtDao.getDebtsByType(userId, type)
            .map { entities -> entities.map { DebtMapper.toModel(it) } }
    }

    override suspend fun addDebt(debt: Debt) {
        val debtWithId = if (debt.id.isEmpty()) {
            debt.copy(id = UUID.randomUUID().toString())
        } else {
            debt
        }
        val entity = DebtMapper.toEntity(debtWithId)
        debtDao.insertDebt(entity)
        
        // Sync to Firestore
        syncDebtToFirestore(debtWithId)
    }

    override suspend fun updateDebt(debt: Debt) {
        val entity = DebtMapper.toEntity(debt)
        debtDao.updateDebt(entity)
        
        // Sync to Firestore
        syncDebtToFirestore(debt)
    }

    override suspend fun deleteDebt(debtId: String) {
        debtDao.deleteDebtById(debtId)
        
        // Delete from Firestore
        try {
            val debt = getDebtById(debtId)
            if (debt != null) {
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(debt.userId)
                    .collection(Constants.COLLECTION_DEBTS)
                    .document(debtId)
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun syncDebtToFirestore(debt: Debt) {
        try {
            val debtMap = mapOf(
                "id" to debt.id,
                "userId" to debt.userId,
                "type" to debt.type,
                "creditorOrDebtor" to debt.creditorOrDebtor,
                "amount" to debt.amount,
                "remainingAmount" to debt.remainingAmount,
                "dueDate" to (debt.dueDate?.let { com.google.firebase.Timestamp(it.time / 1000, ((it.time % 1000) * 1000000).toInt()) } ?: null),
                "notes" to debt.notes,
                "isPaid" to debt.isPaid,
                "createdAt" to com.google.firebase.Timestamp(debt.createdAt.time / 1000, ((debt.createdAt.time % 1000) * 1000000).toInt()),
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
            
            firestore.collection(Constants.COLLECTION_USERS)
                .document(debt.userId)
                .collection(Constants.COLLECTION_DEBTS)
                .document(debt.id)
                .set(debtMap)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

