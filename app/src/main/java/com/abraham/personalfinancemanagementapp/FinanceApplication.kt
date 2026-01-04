package com.abraham.personalfinancemanagementapp

import android.app.Application
import com.abraham.personalfinancemanagementapp.di.AppContainer
import com.google.firebase.FirebaseApp

/**
 * Application class for the Personal Finance Management App
 * This will be used for Hilt dependency injection in the future
 */
class FinanceApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Initialize dependency container
        appContainer = AppContainer(this)
        
        // Initialize WorkManager for recurring transactions
        com.abraham.personalfinancemanagementapp.data.work.WorkManagerInitializer.scheduleRecurringTransactionWork(this)
        
        // Initialize notification channels
        com.abraham.personalfinancemanagementapp.util.NotificationHelper.createNotificationChannels(this)
    }
}
