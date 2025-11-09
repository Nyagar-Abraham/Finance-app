package com.abraham.personalfinancemanagementapp.data.model

import java.util.Date

/**
 * Data model representing a recurring transaction
 */
data class RecurringTransaction(
    val id: String = "",
    val userId: String = "",
    val type: String = "", // "income" or "expense"
    val amount: Double = 0.0,
    val categoryId: String = "",
    val notes: String = "",
    val paymentMethod: String = "",
    val frequency: String = "", // "daily", "weekly", "monthly", "yearly"
    val nextDueDate: Date = Date(),
    val isActive: Boolean = true
)
