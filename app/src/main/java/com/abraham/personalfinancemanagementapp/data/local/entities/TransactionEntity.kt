package com.abraham.personalfinancemanagementapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.abraham.personalfinancemanagementapp.data.local.converters.DateConverter
import com.abraham.personalfinancemanagementapp.data.local.converters.StringListConverter

/**
 * Room entity for Transaction table
 */
@Entity(tableName = "transactions")
@TypeConverters(StringListConverter::class, DateConverter::class)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val type: String, // "income" or "expense"
    val amount: Double,
    val categoryId: String,
    val date: Long,
    val notes: String,
    val paymentMethod: String,
    val tags: List<String>,
    val receiptUrl: String = "",
    val syncStatus: String, // "SYNCED", "PENDING", "FAILED"
    val createdAt: Long,
    val updatedAt: Long
)
