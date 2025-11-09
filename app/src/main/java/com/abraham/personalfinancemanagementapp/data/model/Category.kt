package com.abraham.personalfinancemanagementapp.data.model

/**
 * Data model representing a transaction category
 */
data class Category(
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val color: String = "",
    val isDefault: Boolean = false,
    val userId: String = "",
    val type: String = "" // "income" or "expense"
)
