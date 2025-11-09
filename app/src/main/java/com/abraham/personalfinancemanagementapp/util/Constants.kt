package com.abraham.personalfinancemanagementapp.util

/**
 * Application-wide constants
 */
object Constants {

    // Database
    const val DATABASE_NAME = "personal_finance_db"
    const val DATABASE_VERSION = 1

    // Preferences
    const val PREFS_NAME = "personal_finance_prefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    const val KEY_THEME_MODE = "theme_mode"
    const val KEY_CURRENCY = "currency"

    // Firebase Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_TRANSACTIONS = "transactions"
    const val COLLECTION_CATEGORIES = "categories"
    const val COLLECTION_BUDGETS = "budgets"
    const val COLLECTION_RECURRING_TRANSACTIONS = "recurringTransactions"
    const val COLLECTION_DEBTS = "debts"
    const val COLLECTION_SAVINGS_GOALS = "savingsGoals"

    // Firebase Storage
    const val STORAGE_RECEIPTS = "receipts"
    const val STORAGE_EXPORTS = "exports"

    // Transaction Types
    const val TRANSACTION_TYPE_INCOME = "income"
    const val TRANSACTION_TYPE_EXPENSE = "expense"

    // Recurring Frequencies
    const val FREQUENCY_DAILY = "daily"
    const val FREQUENCY_WEEKLY = "weekly"
    const val FREQUENCY_MONTHLY = "monthly"
    const val FREQUENCY_YEARLY = "yearly"

    // Payment Methods
    const val PAYMENT_METHOD_CASH = "cash"
    const val PAYMENT_METHOD_CARD = "card"
    const val PAYMENT_METHOD_BANK_TRANSFER = "bank_transfer"
    const val PAYMENT_METHOD_DIGITAL_WALLET = "digital_wallet"

    // Budget Alert Thresholds
    const val BUDGET_WARNING_THRESHOLD = 0.8f // 80%
    const val BUDGET_CRITICAL_THRESHOLD = 1.0f // 100%

    // Notification Channels
    const val NOTIFICATION_CHANNEL_BUDGET = "budget_alerts"
    const val NOTIFICATION_CHANNEL_RECURRING = "recurring_transactions"
    const val NOTIFICATION_CHANNEL_BILLS = "bill_reminders"
    const val NOTIFICATION_CHANNEL_SYNC = "sync_status"

    // Work Manager Tags
    const val WORK_TAG_SYNC = "sync_work"
    const val WORK_TAG_RECURRING = "recurring_work"
    const val WORK_TAG_NOTIFICATION = "notification_work"

    // Date Formats
    const val DATE_FORMAT_DISPLAY = "MMM dd, yyyy"
    const val DATE_FORMAT_STORAGE = "yyyy-MM-dd"
    const val DATE_TIME_FORMAT_STORAGE = "yyyy-MM-dd HH:mm:ss"

    // Export
    const val EXPORT_FORMAT_CSV = "csv"
    const val EXPORT_FORMAT_EXCEL = "xlsx"
    const val EXPORT_FORMAT_PDF = "pdf"

    // API
    const val CURRENCY_API_BASE_URL = "https://api.exchangerate-api.com/v4/"

    // Default Categories
    val DEFAULT_EXPENSE_CATEGORIES = listOf(
        "Food & Dining",
        "Transportation",
        "Shopping",
        "Entertainment",
        "Bills & Utilities",
        "Healthcare",
        "Education",
        "Others"
    )

    val DEFAULT_INCOME_CATEGORIES = listOf(
        "Salary",
        "Business",
        "Investments",
        "Others"
    )
}
