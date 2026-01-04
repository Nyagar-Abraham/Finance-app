package com.abraham.personalfinancemanagementapp.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Helper class for managing notifications
 */
object NotificationHelper {
    
    /**
     * Creates all notification channels required by the app
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Budget Alerts Channel
            val budgetChannel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_BUDGET,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for budget warnings and alerts"
                enableVibration(true)
            }
            
            // Recurring Transactions Channel
            val recurringChannel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_RECURRING,
                "Recurring Transactions",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for recurring transaction reminders"
                enableVibration(true)
            }
            
            // Bill Reminders Channel
            val billsChannel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_BILLS,
                "Bill Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for bill payment reminders"
                enableVibration(true)
            }
            
            // Sync Status Channel
            val syncChannel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_SYNC,
                "Sync Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for data synchronization status"
                enableVibration(false)
            }
            
            notificationManager.createNotificationChannel(budgetChannel)
            notificationManager.createNotificationChannel(recurringChannel)
            notificationManager.createNotificationChannel(billsChannel)
            notificationManager.createNotificationChannel(syncChannel)
        }
    }
    
    /**
     * Shows a budget warning notification
     */
    fun showBudgetWarningNotification(
        context: Context,
        categoryName: String,
        budgetAmount: Double,
        spentAmount: Double,
        percentage: Float
    ) {
        val notificationId = categoryName.hashCode()
        val message = if (percentage >= 1.0f) {
            "Budget exceeded! You've spent ${String.format("%.2f", spentAmount)} out of ${String.format("%.2f", budgetAmount)}"
        } else {
            "Budget warning: ${String.format("%.0f", percentage * 100)}% used (${String.format("%.2f", spentAmount)}/${String.format("%.2f", budgetAmount)})"
        }
        
        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_BUDGET)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Budget Alert: $categoryName")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
    
    /**
     * Shows a recurring transaction reminder notification
     */
    fun showRecurringTransactionNotification(
        context: Context,
        transactionId: String,
        title: String,
        message: String
    ) {
        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_RECURRING)
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(transactionId.hashCode(), notification)
    }
    
    /**
     * Shows a bill reminder notification
     */
    fun showBillReminderNotification(
        context: Context,
        billId: String,
        billName: String,
        dueDate: String,
        amount: Double
    ) {
        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_BILLS)
            .setSmallIcon(android.R.drawable.ic_menu_today)
            .setContentTitle("Bill Reminder: $billName")
            .setContentText("Due: $dueDate | Amount: ${String.format("%.2f", amount)}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(billId.hashCode(), notification)
    }
    
    /**
     * Shows a sync status notification
     */
    fun showSyncStatusNotification(
        context: Context,
        isSuccess: Boolean,
        message: String
    ) {
        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_SYNC)
            .setSmallIcon(if (isSuccess) android.R.drawable.ic_menu_upload else android.R.drawable.ic_menu_close_clear_cancel)
            .setContentTitle(if (isSuccess) "Sync Successful" else "Sync Failed")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notification)
    }
}








