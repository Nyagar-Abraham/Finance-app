package com.abraham.personalfinancemanagementapp.data.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.abraham.personalfinancemanagementapp.util.Constants
import java.util.concurrent.TimeUnit

/**
 * Initializes WorkManager for recurring tasks
 */
object WorkManagerInitializer {
    
    /**
     * Schedule recurring transaction processing
     * This runs daily to check for due recurring transactions
     */
    fun scheduleRecurringTransactionWork(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED) // Can work offline
            .build()
        
        val recurringWorkRequest = PeriodicWorkRequestBuilder<RecurringTransactionWorker>(
            1, TimeUnit.DAYS, // Run once per day
            12, TimeUnit.HOURS // With 12-hour flex interval
        )
            .setConstraints(constraints)
            .addTag(Constants.WORK_TAG_RECURRING)
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "recurring_transaction_work",
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing work if already scheduled
            recurringWorkRequest
        )
    }
    
    /**
     * Cancel all recurring transaction work
     */
    fun cancelRecurringTransactionWork(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("recurring_transaction_work")
    }
}








