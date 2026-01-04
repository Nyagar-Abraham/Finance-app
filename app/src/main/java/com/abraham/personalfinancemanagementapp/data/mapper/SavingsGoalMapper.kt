package com.abraham.personalfinancemanagementapp.data.mapper

import com.abraham.personalfinancemanagementapp.data.local.entities.SavingsGoalEntity
import com.abraham.personalfinancemanagementapp.data.model.SavingsGoal
import java.util.Date

/**
 * Mapper functions to convert between SavingsGoal data model and SavingsGoalEntity
 */
object SavingsGoalMapper {

    fun toEntity(savingsGoal: SavingsGoal): SavingsGoalEntity {
        return SavingsGoalEntity(
            id = savingsGoal.id,
            userId = savingsGoal.userId,
            name = savingsGoal.name,
            targetAmount = savingsGoal.targetAmount,
            currentAmount = savingsGoal.currentAmount,
            deadline = savingsGoal.deadline?.time,
            icon = savingsGoal.icon,
            color = savingsGoal.color,
            createdAt = savingsGoal.createdAt.time
        )
    }

    fun toModel(entity: SavingsGoalEntity): SavingsGoal {
        return SavingsGoal(
            id = entity.id,
            userId = entity.userId,
            name = entity.name,
            targetAmount = entity.targetAmount,
            currentAmount = entity.currentAmount,
            deadline = entity.deadline?.let { Date(it) },
            icon = entity.icon,
            color = entity.color,
            createdAt = Date(entity.createdAt)
        )
    }
}








