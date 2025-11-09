package com.abraham.personalfinancemanagementapp.domain.repository

import com.abraham.personalfinancemanagementapp.data.model.User

/**
 * Repository interface for Authentication operations
 */
interface IAuthRepository {

    suspend fun signInWithEmail(email: String, password: String): Result<User>

    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<User>

    suspend fun signInWithGoogle(): Result<User>

    suspend fun signOut(): Result<Unit>

    suspend fun getCurrentUser(): User?

    fun isUserLoggedIn(): Boolean

    suspend fun resetPassword(email: String): Result<Unit>
}
