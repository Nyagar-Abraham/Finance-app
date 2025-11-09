package com.abraham.personalfinancemanagementapp.presentation.navigation

/**
 * Sealed class representing all navigation destinations in the app
 */
sealed class Screen(val route: String) {

    // Onboarding & Auth
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object SignUp : Screen("signup")

    // Main Screens
    data object Home : Screen("home")
    data object TransactionList : Screen("transaction_list")
    data object AddTransaction : Screen("add_transaction")
    data object TransactionDetail : Screen("transaction_detail/{transactionId}") {
        fun createRoute(transactionId: String) = "transaction_detail/$transactionId"
    }

    // Category
    data object CategoryList : Screen("category_list")
    data object AddCategory : Screen("add_category")

    // Budget
    data object BudgetList : Screen("budget_list")
    data object AddBudget : Screen("add_budget")

    // Recurring Transactions
    data object RecurringTransactionList : Screen("recurring_transaction_list")
    data object AddRecurringTransaction : Screen("add_recurring_transaction")

    // Debt
    data object DebtList : Screen("debt_list")
    data object AddDebt : Screen("add_debt")

    // Savings Goals
    data object SavingsGoalList : Screen("savings_goal_list")
    data object AddSavingsGoal : Screen("add_savings_goal")

    // Analytics & Reports
    data object Analytics : Screen("analytics")
    data object Reports : Screen("reports")

    // Settings
    data object Settings : Screen("settings")
}
