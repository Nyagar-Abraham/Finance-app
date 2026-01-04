package com.abraham.personalfinancemanagementapp.data.mapper

import com.abraham.personalfinancemanagementapp.data.local.entities.RecurringTransactionEntity
import com.abraham.personalfinancemanagementapp.data.model.RecurringTransaction
import java.util.Date

/**
 * Mapper functions to convert between RecurringTransaction data model and RecurringTransactionEntity
 */
object RecurringTransactionMapper {

    fun toEntity(recurringTransaction: RecurringTransaction): RecurringTransactionEntity {
        return RecurringTransactionEntity(
            id = recurringTransaction.id,
            userId = recurringTransaction.userId,
            type = recurringTransaction.type,
            amount = recurringTransaction.amount,
            categoryId = recurringTransaction.categoryId,
            notes = recurringTransaction.notes,
            paymentMethod = recurringTransaction.paymentMethod,
            frequency = recurringTransaction.frequency,
            nextDueDate = recurringTransaction.nextDueDate.time,
            isActive = recurringTransaction.isActive
        )
    }

    fun toModel(entity: RecurringTransactionEntity): RecurringTransaction {
        return RecurringTransaction(
            id = entity.id,
            userId = entity.userId,
            type = entity.type,
            amount = entity.amount,
            categoryId = entity.categoryId,
            notes = entity.notes,
            paymentMethod = entity.paymentMethod,
            frequency = entity.frequency,
            nextDueDate = Date(entity.nextDueDate),
            isActive = entity.isActive
        )
    }
}








