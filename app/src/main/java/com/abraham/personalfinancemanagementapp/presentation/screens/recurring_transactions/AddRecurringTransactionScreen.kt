package com.abraham.personalfinancemanagementapp.presentation.screens.recurring_transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abraham.personalfinancemanagementapp.data.model.Category
import com.abraham.personalfinancemanagementapp.presentation.screens.transactions.CategoryChipGroup
import com.abraham.personalfinancemanagementapp.presentation.screens.transactions.PaymentMethodSelector
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddRecurringTransactionUiState
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddRecurringTransactionViewModel
import com.abraham.personalfinancemanagementapp.util.Constants
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecurringTransactionScreen(
    viewModel: AddRecurringTransactionViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val amount by viewModel.amount.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val paymentMethod by viewModel.paymentMethod.collectAsStateWithLifecycle()
    val frequency by viewModel.frequency.collectAsStateWithLifecycle()
    val nextDueDate by viewModel.nextDueDate.collectAsStateWithLifecycle()
    val isActive by viewModel.isActive.collectAsStateWithLifecycle()
    val expenseCategories by viewModel.expenseCategories.collectAsStateWithLifecycle()
    val incomeCategories by viewModel.incomeCategories.collectAsStateWithLifecycle()

    val availableCategories = if (selectedType == Constants.TRANSACTION_TYPE_EXPENSE) {
        expenseCategories
    } else {
        incomeCategories
    }

    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddRecurringTransactionUiState.Success -> {
                onSaveSuccess()
            }
            is AddRecurringTransactionUiState.Error -> {
                // Error is shown in UI
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Recurring Transaction") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Type selector
            Text(
                text = "Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = selectedType == Constants.TRANSACTION_TYPE_EXPENSE,
                    onClick = { viewModel.selectedType.value = Constants.TRANSACTION_TYPE_EXPENSE },
                    label = { Text("Expense") },
                    modifier = Modifier.weight(1f)
                )
                
                FilterChip(
                    selected = selectedType == Constants.TRANSACTION_TYPE_INCOME,
                    onClick = { viewModel.selectedType.value = Constants.TRANSACTION_TYPE_INCOME },
                    label = { Text("Income") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Category selector
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (availableCategories.isEmpty()) {
                Text(
                    text = "No categories available. Please add a category first.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                CategoryChipGroup(
                    categories = availableCategories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category ->
                        viewModel.selectedCategory.value = category
                    }
                )
            }

            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { viewModel.amount.value = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                enabled = !isLoading
            )

            // Frequency
            Text(
                text = "Frequency",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = frequency == Constants.FREQUENCY_DAILY,
                    onClick = { viewModel.frequency.value = Constants.FREQUENCY_DAILY },
                    label = { Text("Daily") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = frequency == Constants.FREQUENCY_WEEKLY,
                    onClick = { viewModel.frequency.value = Constants.FREQUENCY_WEEKLY },
                    label = { Text("Weekly") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = frequency == Constants.FREQUENCY_MONTHLY,
                    onClick = { viewModel.frequency.value = Constants.FREQUENCY_MONTHLY },
                    label = { Text("Monthly") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = frequency == Constants.FREQUENCY_YEARLY,
                    onClick = { viewModel.frequency.value = Constants.FREQUENCY_YEARLY },
                    label = { Text("Yearly") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Next Due Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = dateFormatter.format(nextDueDate),
                    onValueChange = {},
                    label = { Text("Next Due Date") },
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    enabled = !isLoading,
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Date")
                    }
                )
                TextButton(
                    onClick = {
                        // TODO: Show date picker
                    },
                    enabled = !isLoading
                ) {
                    Text("Select")
                }
            }

            // Payment Method
            Text(
                text = "Payment Method",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            PaymentMethodSelector(
                selectedMethod = paymentMethod,
                onMethodSelected = { viewModel.paymentMethod.value = it }
            )

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { viewModel.notes.value = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                enabled = !isLoading
            )

            // Is Active checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isActive,
                    onCheckedChange = { viewModel.isActive.value = it },
                    enabled = !isLoading
                )
                Text(
                    text = "Active",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Error message
            if (uiState is AddRecurringTransactionUiState.Error) {
                Text(
                    text = (uiState as AddRecurringTransactionUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Save button
            Button(
                onClick = { viewModel.saveRecurringTransaction() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading && selectedCategory != null && amount.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

