package com.abraham.personalfinancemanagementapp.data.model

import java.util.Date

/**
 * Data model representing a financial transaction
 */
data class Transaction(
    val id: String = "",
    val userId: String = "",
    val type: String = "", // "income" or "expense"
    val amount: Double = 0.0,
    val categoryId: String = "",
    val date: Date = Date(),
    val notes: String = "",
    val paymentMethod: String = "",
    val tags: List<String> = emptyList(),
    val receiptUrl: String = "",
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class SyncStatus {
    SYNCED,
    PENDING,
    FAILED
}
