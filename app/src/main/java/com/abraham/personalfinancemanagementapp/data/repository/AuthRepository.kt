package com.abraham.personalfinancemanagementapp.data.repository

import com.abraham.personalfinancemanagementapp.data.local.dao.UserDao
import com.abraham.personalfinancemanagementapp.data.mapper.UserMapper
import com.abraham.personalfinancemanagementapp.data.model.User
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Implementation of IAuthRepository using Firebase Auth and Firestore
 */
class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
) : IAuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                val user = createUserFromFirebaseUser(firebaseUser)
                saveUserLocally(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Sign in failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String, name: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                // Update profile with name
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()

                val user = createUserFromFirebaseUser(firebaseUser, name)
                
                // Save to Firestore
                saveUserToFirestore(user)
                
                // Save locally
                saveUserLocally(user)
                
                Result.success(user)
            } else {
                Result.failure(Exception("Sign up failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(): Result<User> {
        // Google Sign-In will be handled in the ViewModel/Activity
        // This method can be called after getting the ID token
        return Result.failure(Exception("Google Sign-In should be handled via Activity result"))
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser
        return if (firebaseUser != null) {
            // Try to get from local DB first
            val localUser = userDao.getUserById(firebaseUser.uid)
            if (localUser != null) {
                UserMapper.toModel(localUser)
            } else {
                // If not in local DB, create from Firebase user
                createUserFromFirebaseUser(firebaseUser)
            }
        } else {
            null
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign in with Google ID token (called from Activity after Google Sign-In)
     */
    suspend fun signInWithGoogleIdToken(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                val user = createUserFromFirebaseUser(firebaseUser)
                
                // Save to Firestore
                saveUserToFirestore(user)
                
                // Save locally
                saveUserLocally(user)
                
                Result.success(user)
            } else {
                Result.failure(Exception("Google Sign-In failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createUserFromFirebaseUser(
        firebaseUser: FirebaseUser,
        name: String? = null
    ): User {
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            name = name ?: firebaseUser.displayName ?: "",
            photoUrl = firebaseUser.photoUrl?.toString() ?: "",
            currency = "USD",
            createdAt = Date()
        )
    }

    private suspend fun saveUserToFirestore(user: User) {
        try {
            val userMap = mapOf(
                "id" to user.id,
                "email" to user.email,
                "name" to user.name,
                "photoUrl" to user.photoUrl,
                "currency" to user.currency,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            firestore.collection(Constants.COLLECTION_USERS)
                .document(user.id)
                .set(userMap)
                .await()
        } catch (e: Exception) {
            // Log error but don't fail the operation
            e.printStackTrace()
        }
    }

    private suspend fun saveUserLocally(user: User) {
        try {
            val entity = UserMapper.toEntity(user)
            userDao.insertUser(entity)
        } catch (e: Exception) {
            // Log error but don't fail the operation
            e.printStackTrace()
        }
    }
}

