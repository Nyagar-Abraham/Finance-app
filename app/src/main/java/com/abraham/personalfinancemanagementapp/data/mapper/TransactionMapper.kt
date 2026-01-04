package com.abraham.personalfinancemanagementapp.data.mapper

import com.abraham.personalfinancemanagementapp.data.local.entities.TransactionEntity
import com.abraham.personalfinancemanagementapp.data.model.SyncStatus
import com.abraham.personalfinancemanagementapp.data.model.Transaction

/**
 * Mapper functions to convert between Transaction data model and TransactionEntity
 */
object TransactionMapper {

    fun toEntity(transaction: Transaction): TransactionEntity {
        return TransactionEntity(
            id = transaction.id,
            userId = transaction.userId,
            type = transaction.type,
            amount = transaction.amount,
            categoryId = transaction.categoryId,
            date = transaction.date.time,
            notes = transaction.notes,
            paymentMethod = transaction.paymentMethod,
            tags = transaction.tags,
            receiptUrl = transaction.receiptUrl,
            syncStatus = transaction.syncStatus.name,
            createdAt = transaction.createdAt.time,
            updatedAt = transaction.updatedAt.time
        )
    }

    fun toModel(entity: TransactionEntity): Transaction {
        return Transaction(
            id = entity.id,
            userId = entity.userId,
            type = entity.type,
            amount = entity.amount,
            categoryId = entity.categoryId,
            date = java.util.Date(entity.date),
            notes = entity.notes,
            paymentMethod = entity.paymentMethod,
            tags = entity.tags,
            receiptUrl = entity.receiptUrl,
            syncStatus = SyncStatus.valueOf(entity.syncStatus),
            createdAt = java.util.Date(entity.createdAt),
            updatedAt = java.util.Date(entity.updatedAt)
        )
    }
}














