package com.abraham.personalfinancemanagementapp.data.repository

import com.abraham.personalfinancemanagementapp.data.local.dao.CategoryDao
import com.abraham.personalfinancemanagementapp.data.local.entities.CategoryEntity
import com.abraham.personalfinancemanagementapp.data.mapper.CategoryMapper
import com.abraham.personalfinancemanagementapp.data.model.Category
import com.abraham.personalfinancemanagementapp.domain.repository.ICategoryRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Implementation of ICategoryRepository using Room and Firestore
 */
class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val firestore: FirebaseFirestore
) : ICategoryRepository {

    override fun getAllCategories(userId: String): Flow<List<Category>> {
        return categoryDao.getAllCategories(userId)
            .map { entities -> entities.map { CategoryMapper.toModel(it) } }
    }

    override suspend fun getCategoryById(id: String): Category? {
        val entity = categoryDao.getCategoryById(id)
        return entity?.let { CategoryMapper.toModel(it) }
    }

    override fun getCategoriesByType(userId: String, type: String): Flow<List<Category>> {
        return categoryDao.getCategoriesByType(userId, type)
            .map { entities -> entities.map { CategoryMapper.toModel(it) } }
    }

    override suspend fun addCategory(category: Category) {
        val entity = CategoryMapper.toEntity(category)
        categoryDao.insertCategory(entity)
        
        // Sync to Firestore
        syncCategoryToFirestore(category)
    }

    override suspend fun updateCategory(category: Category) {
        val entity = CategoryMapper.toEntity(category)
        categoryDao.updateCategory(entity)
        
        // Sync to Firestore
        syncCategoryToFirestore(category)
    }

    override suspend fun deleteCategory(categoryId: String) {
        categoryDao.deleteCategoryById(categoryId)
        
        // Delete from Firestore
        try {
            val category = getCategoryById(categoryId)
            if (category != null && !category.isDefault) {
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(category.userId)
                    .collection(Constants.COLLECTION_CATEGORIES)
                    .document(categoryId)
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun initializeDefaultCategories(userId: String) {
        // Check if categories already exist
        val existingCategories = categoryDao.getAllCategories(userId).first()
        
        if (existingCategories.isNotEmpty()) return
        
        val defaultCategories = mutableListOf<CategoryEntity>()
        
        // Add default expense categories
        Constants.DEFAULT_EXPENSE_CATEGORIES.forEach { name ->
            defaultCategories.add(
                CategoryEntity(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    icon = "shopping_cart", // Default icon
                    color = "#FF6B35", // Default color
                    isDefault = true,
                    userId = userId,
                    type = Constants.TRANSACTION_TYPE_EXPENSE
                )
            )
        }
        
        // Add default income categories
        Constants.DEFAULT_INCOME_CATEGORIES.forEach { name ->
            defaultCategories.add(
                CategoryEntity(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    icon = "account_balance_wallet", // Default icon
                    color = "#10B981", // Default color
                    isDefault = true,
                    userId = userId,
                    type = Constants.TRANSACTION_TYPE_INCOME
                )
            )
        }
        
        categoryDao.insertCategories(defaultCategories)
    }

    private suspend fun syncCategoryToFirestore(category: Category) {
        try {
            val categoryMap = mapOf(
                "id" to category.id,
                "name" to category.name,
                "icon" to category.icon,
                "color" to category.color,
                "isDefault" to category.isDefault,
                "userId" to category.userId,
                "type" to category.type
            )
            
            firestore.collection(Constants.COLLECTION_USERS)
                .document(category.userId)
                .collection(Constants.COLLECTION_CATEGORIES)
                .document(category.id)
                .set(categoryMap)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

