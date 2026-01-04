package com.abraham.personalfinancemanagementapp.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.abraham.personalfinancemanagementapp.data.mapper.RecurringTransactionMapper
import com.abraham.personalfinancemanagementapp.data.mapper.TransactionMapper
import com.abraham.personalfinancemanagementapp.data.model.Transaction
import com.abraham.personalfinancemanagementapp.data.model.SyncStatus
import com.abraham.personalfinancemanagementapp.di.AppContainer
import com.abraham.personalfinancemanagementapp.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Worker that processes recurring transactions and creates actual transactions
 */
class RecurringTransactionWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val appContainer = (applicationContext.applicationContext as com.abraham.personalfinancemanagementapp.FinanceApplication).appContainer
            val database = appContainer.database
            val recurringTransactionDao = database.recurringTransactionDao()
            val transactionDao = database.transactionDao()
            val authRepository = appContainer.authRepository
            
            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                return@withContext Result.success() // No user logged in, skip
            }
            
            val now = Date()
            val currentTime = now.time
            
            // Get all active recurring transactions for the current user
            val recurringList = recurringTransactionDao.getAllRecurringTransactions(currentUser.id).first()
            
            recurringList.forEach { recurringEntity ->
                val recurring = RecurringTransactionMapper.toModel(recurringEntity)
                
                // Check if it's time to process this recurring transaction
                if (recurring.isActive && recurring.nextDueDate.time <= currentTime) {
                    // Create a new transaction from the recurring transaction
                    val newTransaction = Transaction(
                        id = UUID.randomUUID().toString(),
                        userId = recurring.userId,
                        type = recurring.type, // "income" or "expense"
                        amount = recurring.amount,
                        categoryId = recurring.categoryId,
                        date = recurring.nextDueDate,
                        notes = recurring.notes,
                        paymentMethod = recurring.paymentMethod,
                        tags = emptyList(),
                        receiptUrl = "",
                        syncStatus = SyncStatus.PENDING,
                        createdAt = now,
                        updatedAt = now
                    )
                    
                    // Save the transaction
                    transactionDao.insertTransaction(
                        TransactionMapper.toEntity(newTransaction)
                    )
                    
                    // Calculate next due date based on frequency
                    val nextDueDate = calculateNextDueDate(recurring.nextDueDate, recurring.frequency)
                    
                    // Update recurring transaction with new next due date
                    val updatedRecurring = recurring.copy(nextDueDate = nextDueDate)
                    recurringTransactionDao.updateRecurringTransaction(
                        RecurringTransactionMapper.toEntity(updatedRecurring)
                    )
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
    
    private fun calculateNextDueDate(currentDate: Date, frequency: String): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        
        when (frequency) {
            Constants.FREQUENCY_DAILY -> calendar.add(Calendar.DAY_OF_MONTH, 1)
            Constants.FREQUENCY_WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            Constants.FREQUENCY_MONTHLY -> calendar.add(Calendar.MONTH, 1)
            Constants.FREQUENCY_YEARLY -> calendar.add(Calendar.YEAR, 1)
        }
        
        return calendar.time
    }
}

