package com.abraham.personalfinancemanagementapp.data.mapper

import com.abraham.personalfinancemanagementapp.data.local.entities.BudgetEntity
import com.abraham.personalfinancemanagementapp.data.model.Budget

/**
 * Mapper functions to convert between Budget data model and BudgetEntity
 */
object BudgetMapper {

    fun toEntity(budget: Budget): BudgetEntity {
        return BudgetEntity(
            id = budget.id,
            userId = budget.userId,
            categoryId = budget.categoryId,
            amount = budget.amount,
            month = budget.month,
            year = budget.year,
            spent = budget.spent
        )
    }

    fun toModel(entity: BudgetEntity): Budget {
        return Budget(
            id = entity.id,
            userId = entity.userId,
            categoryId = entity.categoryId,
            amount = entity.amount,
            month = entity.month,
            year = entity.year,
            spent = entity.spent
        )
    }
}

