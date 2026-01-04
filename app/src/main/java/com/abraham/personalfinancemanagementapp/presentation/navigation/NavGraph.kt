package com.abraham.personalfinancemanagementapp.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abraham.personalfinancemanagementapp.util.BiometricHelper
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.presentation.components.BottomNavigationBar
import com.abraham.personalfinancemanagementapp.presentation.screens.auth.LoginScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.auth.SignUpScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.home.HomeScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.onboarding.OnboardingScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.budgets.BudgetListScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.reports.ReportsScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.settings.SettingsScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.transactions.AddTransactionScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.transactions.TransactionListScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.debts.DebtListScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.debts.AddDebtScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.savings_goals.SavingsGoalListScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.savings_goals.AddSavingsGoalScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.recurring_transactions.RecurringTransactionListScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.recurring_transactions.AddRecurringTransactionScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.budgets.AddBudgetScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.categories.CategoryListScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.categories.AddCategoryScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.transactions.TransactionDetailScreen
import com.abraham.personalfinancemanagementapp.presentation.screens.analytics.AnalyticsScreen
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddTransactionViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.BudgetListViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.ReportsViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.SettingsViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AuthViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.HomeViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.TransactionListViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.DebtListViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddDebtViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.SavingsGoalListViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddSavingsGoalViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.RecurringTransactionListViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddRecurringTransactionViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddBudgetViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.CategoryListViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddCategoryViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.TransactionDetailViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AnalyticsViewModel
import com.abraham.personalfinancemanagementapp.domain.repository.IBudgetRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ICategoryRepository
import com.abraham.personalfinancemanagementapp.domain.repository.IDebtRepository
import com.abraham.personalfinancemanagementapp.domain.repository.IRecurringTransactionRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ISavingsGoalRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ITransactionRepository

@Composable
fun NavGraph(
    navController: NavHostController,
    authRepository: IAuthRepository,
    transactionRepository: ITransactionRepository,
    categoryRepository: ICategoryRepository,
    budgetRepository: IBudgetRepository,
    debtRepository: IDebtRepository,
    savingsGoalRepository: ISavingsGoalRepository,
    recurringTransactionRepository: IRecurringTransactionRepository,
    preferencesManager: com.abraham.personalfinancemanagementapp.data.local.PreferencesManager,
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStartedClick = {
                    // Navigate to sign up
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    // Navigate to login
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val viewModel: AuthViewModel = viewModel {
                AuthViewModel(authRepository)
            }
            
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    // Navigate to home after successful login
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.Login.route)
                    }
                },
                onForgotPasswordClick = {
                    // TODO: Implement forgot password screen
                }
            )
        }

        composable(Screen.SignUp.route) {
            val viewModel: AuthViewModel = viewModel {
                AuthViewModel(authRepository)
            }
            
            SignUpScreen(
                viewModel = viewModel,
                onSignUpSuccess = {
                    // Navigate to home after successful sign up
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.SignUp.route)
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            HomeScreenWithBottomNav(
                navController = navController,
                currentRoute = currentRoute,
                authRepository = authRepository,
                transactionRepository = transactionRepository,
                categoryRepository = categoryRepository,
                budgetRepository = budgetRepository,
                debtRepository = debtRepository,
                savingsGoalRepository = savingsGoalRepository,
                recurringTransactionRepository = recurringTransactionRepository,
                preferencesManager = preferencesManager
            )
        }
        
        composable(Screen.TransactionList.route) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            val viewModel: TransactionListViewModel = viewModel {
                TransactionListViewModel(authRepository, transactionRepository)
            }
            
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onAddClick = {
                            navController.navigate(Screen.AddTransaction.route)
                        }
                    )
                }
            ) { paddingValues ->
                TransactionListScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onAddClick = {
                        navController.navigate(Screen.AddTransaction.route)
                    },
                    onTransactionClick = { transactionId ->
                        navController.navigate(Screen.TransactionDetail.createRoute(transactionId))
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
        
        composable(Screen.Reports.route) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val viewModel: ReportsViewModel = viewModel {
                ReportsViewModel(authRepository, transactionRepository, categoryRepository, budgetRepository)
            }

            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onAddClick = {
                            navController.navigate(Screen.AddTransaction.route)
                        }
                    )
                }
            ) { paddingValues ->
                ReportsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onAnalyticsClick = {
                        navController.navigate(Screen.Analytics.route)
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
        
        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            
            val viewModel: TransactionDetailViewModel = viewModel {
                TransactionDetailViewModel(transactionRepository, categoryRepository)
            }
            
            TransactionDetailScreen(
                viewModel = viewModel,
                transactionId = transactionId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { id ->
                    navController.navigate(Screen.AddTransaction.route)
                }
            )
        }
        
        composable(Screen.Analytics.route) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            val viewModel: AnalyticsViewModel = viewModel {
                AnalyticsViewModel(authRepository, transactionRepository, categoryRepository)
            }
            
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onAddClick = {
                            navController.navigate(Screen.AddTransaction.route)
                        }
                    )
                }
            ) { paddingValues ->
                AnalyticsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
        
        composable(Screen.BudgetList.route) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val viewModel: BudgetListViewModel = viewModel {
                BudgetListViewModel(authRepository, budgetRepository, transactionRepository)
            }

            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onAddClick = {
                            navController.navigate(Screen.AddTransaction.route)
                        }
                    )
                }
            ) { paddingValues ->
                BudgetListScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onAddClick = {
                        navController.navigate(Screen.AddBudget.route)
                    },
                    onCategoryClick = {
                        navController.navigate(Screen.CategoryList.route)
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
        
        composable(Screen.Settings.route) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val viewModel: SettingsViewModel = viewModel {
                SettingsViewModel(
                    authRepository = authRepository,
                    transactionRepository = transactionRepository,
                    preferencesManager = preferencesManager
                )
            }

            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onAddClick = {
                            navController.navigate(Screen.AddTransaction.route)
                        }
                    )
                }
            ) { paddingValues ->
                SettingsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        composable(Screen.AddTransaction.route) {
            val viewModel: AddTransactionViewModel = viewModel {
                AddTransactionViewModel(
                    authRepository = authRepository,
                    transactionRepository = transactionRepository,
                    categoryRepository = categoryRepository
                )
            }
            
            AddTransactionScreen(
                viewModel = viewModel,
                authRepository = authRepository,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.DebtList.route) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            val viewModel: DebtListViewModel = viewModel {
                DebtListViewModel(authRepository, debtRepository)
            }
            
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onAddClick = {
                            navController.navigate(Screen.AddTransaction.route)
                        }
                    )
                }
            ) { paddingValues ->
                DebtListScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onAddClick = {
                        navController.navigate(Screen.AddDebt.route)
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
        
        composable(Screen.AddDebt.route) {
            val viewModel: AddDebtViewModel = viewModel {
                AddDebtViewModel(authRepository, debtRepository)
            }
            
            AddDebtScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.SavingsGoalList.route) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            val viewModel: SavingsGoalListViewModel = viewModel {
                SavingsGoalListViewModel(authRepository, savingsGoalRepository)
            }
            
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onAddClick = {
                            navController.navigate(Screen.AddTransaction.route)
                        }
                    )
                }
            ) { paddingValues ->
                SavingsGoalListScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onAddClick = {
                        navController.navigate(Screen.AddSavingsGoal.route)
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
        
        composable(Screen.AddSavingsGoal.route) {
            val viewModel: AddSavingsGoalViewModel = viewModel {
                AddSavingsGoalViewModel(authRepository, savingsGoalRepository)
            }
            
            AddSavingsGoalScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.RecurringTransactionList.route) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            val viewModel: RecurringTransactionListViewModel = viewModel {
                RecurringTransactionListViewModel(authRepository, recurringTransactionRepository)
            }
            
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onAddClick = {
                            navController.navigate(Screen.AddTransaction.route)
                        }
                    )
                }
            ) { paddingValues ->
                RecurringTransactionListScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onAddClick = {
                        navController.navigate(Screen.AddRecurringTransaction.route)
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
        
        composable(Screen.AddRecurringTransaction.route) {
            val viewModel: AddRecurringTransactionViewModel = viewModel {
                AddRecurringTransactionViewModel(
                    authRepository = authRepository,
                    recurringTransactionRepository = recurringTransactionRepository,
                    categoryRepository = categoryRepository
                )
            }
            
            AddRecurringTransactionScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.AddBudget.route) {
            val viewModel: AddBudgetViewModel = viewModel {
                AddBudgetViewModel(
                    authRepository = authRepository,
                    budgetRepository = budgetRepository,
                    categoryRepository = categoryRepository
                )
            }
            
            AddBudgetScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.CategoryList.route) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            val viewModel: CategoryListViewModel = viewModel {
                CategoryListViewModel(authRepository, categoryRepository)
            }
            
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onAddClick = {
                            navController.navigate(Screen.AddTransaction.route)
                        }
                    )
                }
            ) { paddingValues ->
                CategoryListScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onAddClick = {
                        navController.navigate(Screen.AddCategory.route)
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
        
        composable(Screen.AddCategory.route) {
            val viewModel: AddCategoryViewModel = viewModel {
                AddCategoryViewModel(authRepository, categoryRepository)
            }
            
            AddCategoryScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun HomeScreenWithBottomNav(
    navController: NavHostController,
    currentRoute: String?,
    authRepository: IAuthRepository,
    transactionRepository: ITransactionRepository,
    categoryRepository: ICategoryRepository,
    budgetRepository: IBudgetRepository,
    debtRepository: IDebtRepository,
    savingsGoalRepository: ISavingsGoalRepository,
    recurringTransactionRepository: IRecurringTransactionRepository,
    preferencesManager: com.abraham.personalfinancemanagementapp.data.local.PreferencesManager
) {
    val context = LocalContext.current
    val biometricEnabled by preferencesManager.biometricEnabled.collectAsStateWithLifecycle(initialValue = false)
    var showBiometricPrompt by remember { mutableStateOf(false) }
    var isAuthenticated by remember { mutableStateOf(false) }
    
    // Check if biometric is enabled and available
    LaunchedEffect(biometricEnabled) {
        if (biometricEnabled && context is FragmentActivity) {
            val isAvailable = BiometricHelper.isBiometricAvailable(context)
            if (isAvailable && !isAuthenticated) {
                showBiometricPrompt = true
            } else {
                isAuthenticated = true
            }
        } else {
            isAuthenticated = true
        }
    }
    
    // Show biometric prompt if needed
    if (showBiometricPrompt && context is FragmentActivity && !isAuthenticated) {
        BiometricHelper.showBiometricPrompt(
            activity = context,
            title = "Biometric Authentication",
            subtitle = "Please authenticate to access your finances",
            negativeButtonText = "Cancel",
            onSuccess = {
                isAuthenticated = true
                showBiometricPrompt = false
            },
            onError = { error ->
                // On error, navigate back to login
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onCancel = {
                // On cancel, navigate back to login
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
        showBiometricPrompt = false
    }
    
    // Only show content if authenticated or biometric is disabled
    if (!biometricEnabled || isAuthenticated) {
        val homeViewModel: HomeViewModel = viewModel {
            HomeViewModel(authRepository, transactionRepository)
        }
        
        Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onAddClick = {
                    navController.navigate(Screen.AddTransaction.route)
                }
            )
        }
    ) { paddingValues ->
        HomeScreen(
            viewModel = homeViewModel,
            modifier = Modifier.padding(paddingValues),
            onAddClick = {
                navController.navigate(Screen.AddTransaction.route)
            },
            onGoalsClick = {
                navController.navigate(Screen.SavingsGoalList.route)
            },
            onBudgetClick = {
                navController.navigate(Screen.BudgetList.route)
            },
            onDebtsClick = {
                navController.navigate(Screen.DebtList.route)
            },
            onRecurringClick = {
                navController.navigate(Screen.RecurringTransactionList.route)
            },
            onAnalyticsClick = {
                navController.navigate(Screen.Analytics.route)
            },
            onTransactionClick = { transactionId ->
                navController.navigate(Screen.TransactionDetail.createRoute(transactionId))
            }
        )
        }
    } else {
        // Show loading while checking biometric
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

