package com.abraham.personalfinancemanagementapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Category table
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val isDefault: Boolean = false,
    val userId: String,
    val type: String // "income" or "expense"
)
