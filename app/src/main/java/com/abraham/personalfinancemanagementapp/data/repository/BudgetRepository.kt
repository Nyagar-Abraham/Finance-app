package com.abraham.personalfinancemanagementapp.data.repository

import com.abraham.personalfinancemanagementapp.data.local.dao.BudgetDao
import com.abraham.personalfinancemanagementapp.data.mapper.BudgetMapper
import com.abraham.personalfinancemanagementapp.data.model.Budget
import com.abraham.personalfinancemanagementapp.domain.repository.IBudgetRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Implementation of IBudgetRepository using Room and Firestore
 */
class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val firestore: FirebaseFirestore
) : IBudgetRepository {

    override fun getAllBudgets(userId: String): Flow<List<Budget>> {
        return budgetDao.getAllBudgets(userId)
            .map { entities -> entities.map { BudgetMapper.toModel(it) } }
    }

    override suspend fun getBudgetById(id: String): Budget? {
        val entity = budgetDao.getBudgetById(id)
        return entity?.let { BudgetMapper.toModel(it) }
    }

    override fun getBudgetsByMonthYear(userId: String, month: Int, year: Int): Flow<List<Budget>> {
        return budgetDao.getBudgetsByMonthYear(userId, month, year)
            .map { entities -> entities.map { BudgetMapper.toModel(it) } }
    }

    override suspend fun getBudgetByCategoryMonthYear(
        userId: String,
        categoryId: String,
        month: Int,
        year: Int
    ): Budget? {
        val entity = budgetDao.getBudgetByCategoryMonthYear(userId, categoryId, month, year)
        return entity?.let { BudgetMapper.toModel(it) }
    }

    override suspend fun addBudget(budget: Budget) {
        val entity = BudgetMapper.toEntity(budget)
        budgetDao.insertBudget(entity)
        
        // Sync to Firestore
        syncBudgetToFirestore(budget)
    }

    override suspend fun updateBudget(budget: Budget) {
        val entity = BudgetMapper.toEntity(budget)
        budgetDao.updateBudget(entity)
        
        // Sync to Firestore
        syncBudgetToFirestore(budget)
    }

    override suspend fun deleteBudget(budgetId: String) {
        budgetDao.deleteBudgetById(budgetId)
        
        // Delete from Firestore
        try {
            val budget = getBudgetById(budgetId)
            if (budget != null) {
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(budget.userId)
                    .collection(Constants.COLLECTION_BUDGETS)
                    .document(budgetId)
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun syncBudgetToFirestore(budget: Budget) {
        try {
            val budgetMap = mapOf(
                "id" to budget.id,
                "userId" to budget.userId,
                "categoryId" to budget.categoryId,
                "amount" to budget.amount,
                "month" to budget.month,
                "year" to budget.year,
                "spent" to budget.spent,
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
            
            firestore.collection(Constants.COLLECTION_USERS)
                .document(budget.userId)
                .collection(Constants.COLLECTION_BUDGETS)
                .document(budget.id)
                .set(budgetMap)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

