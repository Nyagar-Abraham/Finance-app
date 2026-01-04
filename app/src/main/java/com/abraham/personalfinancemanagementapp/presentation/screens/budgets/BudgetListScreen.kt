package com.abraham.personalfinancemanagementapp.presentation.screens.budgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.BudgetListViewModel
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.BudgetWithSpending
import com.abraham.personalfinancemanagementapp.util.Constants
import com.abraham.personalfinancemanagementapp.util.NotificationHelper
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetListScreen(
    viewModel: BudgetListViewModel,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onCategoryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val budgetsWithSpending by viewModel.budgetsWithSpending.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Check for budget alerts
    LaunchedEffect(budgetsWithSpending) {
        budgetsWithSpending.forEach { budgetWithSpending ->
            val percentage = budgetWithSpending.percentage
            if (percentage >= Constants.BUDGET_WARNING_THRESHOLD) {
                NotificationHelper.showBudgetWarningNotification(
                    context = context,
                    categoryName = budgetWithSpending.budget.categoryId, // Category name would need to be fetched
                    budgetAmount = budgetWithSpending.budget.amount,
                    spentAmount = budgetWithSpending.spent,
                    percentage = percentage
                )
            }
        }
    }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-KE"))
    val monthNames = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budgets") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onAddClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add Budget")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Month/Year selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${monthNames[selectedMonth - 1]} $selectedYear",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    // TODO: Add month/year picker
                }
            }

            if (isLoading && budgetsWithSpending.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (budgetsWithSpending.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No budgets for this month",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = onAddClick) {
                            Text("Create Your First Budget")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(budgetsWithSpending) { budgetWithSpending ->
                        BudgetCard(
                            budgetWithSpending = budgetWithSpending,
                            currencyFormatter = currencyFormatter
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetCard(
    budgetWithSpending: BudgetWithSpending,
    currencyFormatter: NumberFormat,
    modifier: Modifier = Modifier
) {
    val budget = budgetWithSpending.budget
    val color = when {
        budgetWithSpending.isOverBudget -> MaterialTheme.colorScheme.error
        budgetWithSpending.isWarning -> Color(0xFFFF9800) // Orange
        else -> Color(0xFF10B981) // Green
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category name
            Text(
                text = budget.categoryId.ifBlank { "Uncategorized" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Progress bar
            LinearProgressIndicator(
                progress = budgetWithSpending.percentage.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )

            // Amounts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Budget",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currencyFormatter.format(budget.amount),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Spent",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currencyFormatter.format(budgetWithSpending.spent),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }

            // Remaining
            if (budgetWithSpending.remaining >= 0) {
                Text(
                    text = "${currencyFormatter.format(budgetWithSpending.remaining)} remaining",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF10B981)
                )
            } else {
                Text(
                    text = "${currencyFormatter.format(-budgetWithSpending.remaining)} over budget",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}







