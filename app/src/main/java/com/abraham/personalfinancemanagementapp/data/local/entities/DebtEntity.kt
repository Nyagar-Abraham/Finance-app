package com.abraham.personalfinancemanagementapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Debt table
 */
@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val type: String, // "owed" or "lent"
    val creditorOrDebtor: String,
    val amount: Double,
    val remainingAmount: Double,
    val dueDate: Long?,
    val notes: String,
    val isPaid: Boolean = false,
    val createdAt: Long
)
