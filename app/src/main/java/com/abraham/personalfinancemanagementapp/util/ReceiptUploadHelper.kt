package com.abraham.personalfinancemanagementapp.util

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*

/**
 * Helper class for uploading receipts to Firebase Storage
 */
object ReceiptUploadHelper {
    
    private val storage = FirebaseStorage.getInstance()
    
    /**
     * Upload receipt image to Firebase Storage
     * @param userId User ID
     * @param transactionId Transaction ID
     * @param imageUri URI of the image to upload
     * @return Result containing the download URL or error
     */
    suspend fun uploadReceipt(
        userId: String,
        transactionId: String,
        imageUri: Uri
    ): Result<String> {
        return try {
            val fileName = "receipt_${transactionId}_${System.currentTimeMillis()}.jpg"
            val storageRef = storage.reference
                .child(Constants.STORAGE_RECEIPTS)
                .child(userId)
                .child(fileName)
            
            val uploadTask = storageRef.putFile(imageUri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete receipt from Firebase Storage
     */
    suspend fun deleteReceipt(receiptUrl: String): Result<Unit> {
        return try {
            val storageRef = storage.getReferenceFromUrl(receiptUrl)
            storageRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}








