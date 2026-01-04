package com.abraham.personalfinancemanagementapp.presentation.screens.budgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddBudgetUiState
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddBudgetViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(
    viewModel: AddBudgetViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val amount by viewModel.amount.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val expenseCategories by viewModel.expenseCategories.collectAsStateWithLifecycle()

    val monthNames = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddBudgetUiState.Success -> {
                onSaveSuccess()
            }
            is AddBudgetUiState.Error -> {
                // Error is shown in UI
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Budget") },
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
            // Category selector
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (expenseCategories.isEmpty()) {
                Text(
                    text = "No expense categories available. Please add a category first.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                CategoryChipGroup(
                    categories = expenseCategories,
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
                label = { Text("Budget Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                enabled = !isLoading
            )

            // Month selector
            Text(
                text = "Month",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                monthNames.forEachIndexed { index, monthName ->
                    FilterChip(
                        selected = selectedMonth == index + 1,
                        onClick = { viewModel.selectedMonth.value = index + 1 },
                        label = { Text(monthName.take(3)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Year selector (simplified - showing current year and next year)
            Text(
                text = "Year",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                for (year in currentYear..currentYear + 1) {
                    FilterChip(
                        selected = selectedYear == year,
                        onClick = { viewModel.selectedYear.value = year },
                        label = { Text(year.toString()) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Error message
            if (uiState is AddBudgetUiState.Error) {
                Text(
                    text = (uiState as AddBudgetUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Save button
            Button(
                onClick = { viewModel.saveBudget() },
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








