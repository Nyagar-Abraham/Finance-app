package com.abraham.personalfinancemanagementapp.data.model

import java.util.Date

/**
 * Data model representing a user
 */
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val currency: String = "USD",
    val createdAt: Date = Date()
)
