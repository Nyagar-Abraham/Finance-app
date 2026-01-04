package com.abraham.personalfinancemanagementapp.data.mapper

import com.abraham.personalfinancemanagementapp.data.local.entities.DebtEntity
import com.abraham.personalfinancemanagementapp.data.model.Debt
import java.util.Date

/**
 * Mapper functions to convert between Debt data model and DebtEntity
 */
object DebtMapper {

    fun toEntity(debt: Debt): DebtEntity {
        return DebtEntity(
            id = debt.id,
            userId = debt.userId,
            type = debt.type,
            creditorOrDebtor = debt.creditorOrDebtor,
            amount = debt.amount,
            remainingAmount = debt.remainingAmount,
            dueDate = debt.dueDate?.time,
            notes = debt.notes,
            isPaid = debt.isPaid,
            createdAt = debt.createdAt.time
        )
    }

    fun toModel(entity: DebtEntity): Debt {
        return Debt(
            id = entity.id,
            userId = entity.userId,
            type = entity.type,
            creditorOrDebtor = entity.creditorOrDebtor,
            amount = entity.amount,
            remainingAmount = entity.remainingAmount,
            dueDate = entity.dueDate?.let { Date(it) },
            notes = entity.notes,
            isPaid = entity.isPaid,
            createdAt = Date(entity.createdAt)
        )
    }
}








