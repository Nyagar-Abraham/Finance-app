package com.abraham.personalfinancemanagementapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Room entity for User table
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val name: String,
    val photoUrl: String = "",
    val currency: String = "KSH",
    val createdAt: Long
)
