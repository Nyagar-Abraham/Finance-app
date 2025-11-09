package com.abraham.personalfinancemanagementapp

import android.app.Application

/**
 * Application class for the Personal Finance Management App
 * This will be used for Hilt dependency injection in the future
 */
class FinanceApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize application-level components here
        // Firebase, WorkManager, Notification Channels, etc.
    }
}
