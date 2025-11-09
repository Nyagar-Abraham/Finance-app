package com.abraham.personalfinancemanagementapp.data.local.dao

import androidx.room.*
import com.abraham.personalfinancemanagementapp.data.local.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Category entity
 */
@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories WHERE userId = :userId OR isDefault = 1")
    fun getAllCategories(userId: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Query("SELECT * FROM categories WHERE (userId = :userId OR isDefault = 1) AND type = :type")
    fun getCategoriesByType(userId: String, type: String): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id AND isDefault = 0")
    suspend fun deleteCategoryById(id: String)
}
