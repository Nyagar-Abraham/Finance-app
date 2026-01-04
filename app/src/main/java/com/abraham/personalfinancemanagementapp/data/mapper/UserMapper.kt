package com.abraham.personalfinancemanagementapp.data.mapper

import com.abraham.personalfinancemanagementapp.data.local.entities.UserEntity
import com.abraham.personalfinancemanagementapp.data.model.User

/**
 * Mapper functions to convert between User data model and UserEntity
 */
object UserMapper {

    fun toEntity(user: User): UserEntity {
        return UserEntity(
            id = user.id,
            email = user.email,
            name = user.name,
            photoUrl = user.photoUrl,
            currency = user.currency,
            createdAt = user.createdAt.time
        )
    }

    fun toModel(entity: UserEntity): User {
        return User(
            id = entity.id,
            email = entity.email,
            name = entity.name,
            photoUrl = entity.photoUrl,
            currency = entity.currency,
            createdAt = java.util.Date(entity.createdAt)
        )
    }
}

