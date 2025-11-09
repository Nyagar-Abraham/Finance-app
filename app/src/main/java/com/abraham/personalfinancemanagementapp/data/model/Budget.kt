package com.abraham.personalfinancemanagementapp.data.model

/**
 * Data model representing a budget
 */
data class Budget(
    val id: String = "",
    val userId: String = "",
    val categoryId: String = "",
    val amount: Double = 0.0,
    val month: Int = 0,
    val year: Int = 0,
    val spent: Double = 0.0
) {
    val progress: Float
        get() = if (amount > 0) (spent / amount).toFloat() else 0f

    val isOverBudget: Boolean
        get() = spent > amount
}
