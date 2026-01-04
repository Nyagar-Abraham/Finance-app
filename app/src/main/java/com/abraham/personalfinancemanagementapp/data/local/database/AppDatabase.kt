package com.abraham.personalfinancemanagementapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abraham.personalfinancemanagementapp.data.local.converters.DateConverter
import com.abraham.personalfinancemanagementapp.data.local.converters.StringListConverter
import com.abraham.personalfinancemanagementapp.data.local.dao.*
import com.abraham.personalfinancemanagementapp.data.local.entities.*

/**
 * Room database for the application
 */
@Database(
    entities = [
        UserEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        RecurringTransactionEntity::class,
        DebtEntity::class,
        SavingsGoalEntity::class
    ],
    version = 1,
    exportSchema = false  // Temporarily disabled due to kotlinx.serialization compatibility issue
)
@TypeConverters(DateConverter::class, StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun recurringTransactionDao(): RecurringTransactionDao
    abstract fun debtDao(): DebtDao
    abstract fun savingsGoalDao(): SavingsGoalDao
}
