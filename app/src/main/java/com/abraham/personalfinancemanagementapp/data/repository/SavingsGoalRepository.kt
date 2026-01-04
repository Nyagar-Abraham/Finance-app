package com.abraham.personalfinancemanagementapp.data.repository

import com.abraham.personalfinancemanagementapp.data.local.dao.SavingsGoalDao
import com.abraham.personalfinancemanagementapp.data.mapper.SavingsGoalMapper
import com.abraham.personalfinancemanagementapp.data.model.SavingsGoal
import com.abraham.personalfinancemanagementapp.domain.repository.ISavingsGoalRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Implementation of ISavingsGoalRepository using Room and Firestore
 */
class SavingsGoalRepository(
    private val savingsGoalDao: SavingsGoalDao,
    private val firestore: FirebaseFirestore
) : ISavingsGoalRepository {

    override fun getAllSavingsGoals(userId: String): Flow<List<SavingsGoal>> {
        return savingsGoalDao.getAllSavingsGoals(userId)
            .map { entities -> entities.map { SavingsGoalMapper.toModel(it) } }
    }

    override suspend fun getSavingsGoalById(id: String): SavingsGoal? {
        val entity = savingsGoalDao.getSavingsGoalById(id)
        return entity?.let { SavingsGoalMapper.toModel(it) }
    }

    override fun getIncompleteSavingsGoals(userId: String): Flow<List<SavingsGoal>> {
        return savingsGoalDao.getIncompleteSavingsGoals(userId)
            .map { entities -> entities.map { SavingsGoalMapper.toModel(it) } }
    }

    override suspend fun addSavingsGoal(savingsGoal: SavingsGoal) {
        val savingsGoalWithId = if (savingsGoal.id.isEmpty()) {
            savingsGoal.copy(id = UUID.randomUUID().toString())
        } else {
            savingsGoal
        }
        val entity = SavingsGoalMapper.toEntity(savingsGoalWithId)
        savingsGoalDao.insertSavingsGoal(entity)
        
        // Sync to Firestore
        syncSavingsGoalToFirestore(savingsGoalWithId)
    }

    override suspend fun updateSavingsGoal(savingsGoal: SavingsGoal) {
        val entity = SavingsGoalMapper.toEntity(savingsGoal)
        savingsGoalDao.updateSavingsGoal(entity)
        
        // Sync to Firestore
        syncSavingsGoalToFirestore(savingsGoal)
    }

    override suspend fun deleteSavingsGoal(savingsGoalId: String) {
        savingsGoalDao.deleteSavingsGoalById(savingsGoalId)
        
        // Delete from Firestore
        try {
            val savingsGoal = getSavingsGoalById(savingsGoalId)
            if (savingsGoal != null) {
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(savingsGoal.userId)
                    .collection(Constants.COLLECTION_SAVINGS_GOALS)
                    .document(savingsGoalId)
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun syncSavingsGoalToFirestore(savingsGoal: SavingsGoal) {
        try {
            val savingsGoalMap = mapOf(
                "id" to savingsGoal.id,
                "userId" to savingsGoal.userId,
                "name" to savingsGoal.name,
                "targetAmount" to savingsGoal.targetAmount,
                "currentAmount" to savingsGoal.currentAmount,
                "deadline" to (savingsGoal.deadline?.let { com.google.firebase.Timestamp(it.time / 1000, ((it.time % 1000) * 1000000).toInt()) } ?: null),
                "icon" to savingsGoal.icon,
                "color" to savingsGoal.color,
                "createdAt" to com.google.firebase.Timestamp(savingsGoal.createdAt.time / 1000, ((savingsGoal.createdAt.time % 1000) * 1000000).toInt()),
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
            
            firestore.collection(Constants.COLLECTION_USERS)
                .document(savingsGoal.userId)
                .collection(Constants.COLLECTION_SAVINGS_GOALS)
                .document(savingsGoal.id)
                .set(savingsGoalMap)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

