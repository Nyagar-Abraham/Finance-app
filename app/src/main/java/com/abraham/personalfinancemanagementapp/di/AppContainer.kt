package com.abraham.personalfinancemanagementapp.di

import android.content.Context
import androidx.room.Room
import com.abraham.personalfinancemanagementapp.data.local.database.AppDatabase
import com.abraham.personalfinancemanagementapp.data.repository.AuthRepository
import com.abraham.personalfinancemanagementapp.data.repository.BudgetRepository
import com.abraham.personalfinancemanagementapp.data.repository.CategoryRepository
import com.abraham.personalfinancemanagementapp.data.repository.DebtRepository
import com.abraham.personalfinancemanagementapp.data.repository.RecurringTransactionRepository
import com.abraham.personalfinancemanagementapp.data.repository.SavingsGoalRepository
import com.abraham.personalfinancemanagementapp.data.repository.TransactionRepository
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.IBudgetRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ICategoryRepository
import com.abraham.personalfinancemanagementapp.domain.repository.IDebtRepository
import com.abraham.personalfinancemanagementapp.domain.repository.IRecurringTransactionRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ISavingsGoalRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ITransactionRepository
import com.abraham.personalfinancemanagementapp.data.local.PreferencesManager
import com.abraham.personalfinancemanagementapp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Simple dependency container for the application
 * This will be replaced with Hilt in the future
 */
class AppContainer(private val context: Context) {

    // Firebase
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Room Database
    val database: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        Constants.DATABASE_NAME
    ).build()

    // Repositories
    val authRepository: IAuthRepository = AuthRepository(
        firebaseAuth = firebaseAuth,
        firestore = firestore,
        userDao = database.userDao()
    )
    
    val transactionRepository: ITransactionRepository = TransactionRepository(
        transactionDao = database.transactionDao(),
        firestore = firestore
    )
    
    val categoryRepository: ICategoryRepository = CategoryRepository(
        categoryDao = database.categoryDao(),
        firestore = firestore
    )
    
    val budgetRepository: IBudgetRepository = BudgetRepository(
        budgetDao = database.budgetDao(),
        firestore = firestore
    )
    
    val debtRepository: IDebtRepository = DebtRepository(
        debtDao = database.debtDao(),
        firestore = firestore
    )
    
    val savingsGoalRepository: ISavingsGoalRepository = SavingsGoalRepository(
        savingsGoalDao = database.savingsGoalDao(),
        firestore = firestore
    )
    
    val recurringTransactionRepository: IRecurringTransactionRepository = RecurringTransactionRepository(
        recurringTransactionDao = database.recurringTransactionDao(),
        firestore = firestore
    )
    
    // Preferences Manager
    val preferencesManager: PreferencesManager = PreferencesManager(context)
}

