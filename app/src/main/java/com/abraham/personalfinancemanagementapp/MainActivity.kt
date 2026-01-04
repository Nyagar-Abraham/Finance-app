package com.abraham.personalfinancemanagementapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.abraham.personalfinancemanagementapp.presentation.navigation.NavGraph
import com.abraham.personalfinancemanagementapp.ui.theme.PersonalFinanceManagementAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val appContainer = (application as FinanceApplication).appContainer
        
        setContent {
            val themeMode by appContainer.preferencesManager.themeMode.collectAsState(initial = "system")
            val isSystemInDarkTheme = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme
            }
            
            PersonalFinanceManagementAppTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        authRepository = appContainer.authRepository,
                        transactionRepository = appContainer.transactionRepository,
                        categoryRepository = appContainer.categoryRepository,
                        budgetRepository = appContainer.budgetRepository,
                        debtRepository = appContainer.debtRepository,
                        savingsGoalRepository = appContainer.savingsGoalRepository,
                        recurringTransactionRepository = appContainer.recurringTransactionRepository,
                        preferencesManager = appContainer.preferencesManager
                    )
                }
            }
        }
    }
}