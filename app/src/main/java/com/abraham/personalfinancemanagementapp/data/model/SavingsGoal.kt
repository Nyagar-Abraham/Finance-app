package com.abraham.personalfinancemanagementapp.data.model

import java.util.Date

/**
 * Data model representing a savings goal
 */
data class SavingsGoal(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val deadline: Date? = null,
    val icon: String = "",
    val color: String = "",
    val createdAt: Date = Date()
) {
    val progress: Float
        get() = if (targetAmount > 0) (currentAmount / targetAmount).toFloat() else 0f

    val isCompleted: Boolean
        get() = currentAmount >= targetAmount
}
