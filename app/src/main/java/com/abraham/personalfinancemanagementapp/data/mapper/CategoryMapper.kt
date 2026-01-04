package com.abraham.personalfinancemanagementapp.data.mapper

import com.abraham.personalfinancemanagementapp.data.local.entities.CategoryEntity
import com.abraham.personalfinancemanagementapp.data.model.Category

/**
 * Mapper functions to convert between Category data model and CategoryEntity
 */
object CategoryMapper {

    fun toEntity(category: Category): CategoryEntity {
        return CategoryEntity(
            id = category.id,
            name = category.name,
            icon = category.icon,
            color = category.color,
            isDefault = category.isDefault,
            userId = category.userId,
            type = category.type
        )
    }

    fun toModel(entity: CategoryEntity): Category {
        return Category(
            id = entity.id,
            name = entity.name,
            icon = entity.icon,
            color = entity.color,
            isDefault = entity.isDefault,
            userId = entity.userId,
            type = entity.type
        )
    }
}














