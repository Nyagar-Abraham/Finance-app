package com.abraham.personalfinancemanagementapp.domain.repository

import com.abraham.personalfinancemanagementapp.data.model.Debt
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Debt operations
 */
interface IDebtRepository {

    fun getAllDebts(userId: String): Flow<List<Debt>>

    suspend fun getDebtById(id: String): Debt?

    fun getUnpaidDebts(userId: String): Flow<List<Debt>>

    fun getDebtsByType(userId: String, type: String): Flow<List<Debt>>

    suspend fun addDebt(debt: Debt)

    suspend fun updateDebt(debt: Debt)

    suspend fun deleteDebt(debtId: String)
}








