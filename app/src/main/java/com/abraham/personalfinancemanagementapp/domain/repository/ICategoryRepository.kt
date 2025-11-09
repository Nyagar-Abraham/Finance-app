package com.abraham.personalfinancemanagementapp.domain.repository

import com.abraham.personalfinancemanagementapp.data.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Category operations
 */
interface ICategoryRepository {

    fun getAllCategories(userId: String): Flow<List<Category>>

    suspend fun getCategoryById(id: String): Category?

    fun getCategoriesByType(userId: String, type: String): Flow<List<Category>>

    suspend fun addCategory(category: Category)

    suspend fun updateCategory(category: Category)

    suspend fun deleteCategory(categoryId: String)

    suspend fun initializeDefaultCategories(userId: String)
}
