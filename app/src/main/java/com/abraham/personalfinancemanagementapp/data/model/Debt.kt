package com.abraham.personalfinancemanagementapp.data.model

import java.util.Date

/**
 * Data model representing a debt
 */
data class Debt(
    val id: String = "",
    val userId: String = "",
    val type: String = "", // "owed" (I owe) or "lent" (someone owes me)
    val creditorOrDebtor: String = "", // Name of person/institution
    val amount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val dueDate: Date? = null,
    val notes: String = "",
    val isPaid: Boolean = false,
    val createdAt: Date = Date()
)
