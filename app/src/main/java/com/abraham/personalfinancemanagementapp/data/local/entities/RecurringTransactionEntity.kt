package com.abraham.personalfinancemanagementapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for RecurringTransaction table
 */
@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val type: String, // "income" or "expense"
    val amount: Double,
    val categoryId: String,
    val notes: String,
    val paymentMethod: String,
    val frequency: String, // "daily", "weekly", "monthly", "yearly"
    val nextDueDate: Long,
    val isActive: Boolean = true
)
