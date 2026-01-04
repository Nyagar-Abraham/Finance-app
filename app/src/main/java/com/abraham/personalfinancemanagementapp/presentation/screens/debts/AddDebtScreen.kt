package com.abraham.personalfinancemanagementapp.presentation.screens.debts

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddDebtUiState
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddDebtViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDebtScreen(
    viewModel: AddDebtViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val creditorOrDebtor by viewModel.creditorOrDebtor.collectAsStateWithLifecycle()
    val amount by viewModel.amount.collectAsStateWithLifecycle()
    val remainingAmount by viewModel.remainingAmount.collectAsStateWithLifecycle()
    val dueDate by viewModel.dueDate.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val isPaid by viewModel.isPaid.collectAsStateWithLifecycle()

    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddDebtUiState.Success -> {
                onSaveSuccess()
            }
            is AddDebtUiState.Error -> {
                // Error is shown in UI
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Debt") },
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
                    selected = selectedType == "owed",
                    onClick = { viewModel.selectedType.value = "owed" },
                    label = { Text("I Owe") },
                    modifier = Modifier.weight(1f)
                )
                
                FilterChip(
                    selected = selectedType == "lent",
                    onClick = { viewModel.selectedType.value = "lent" },
                    label = { Text("Owed to Me") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Creditor/Debtor name
            OutlinedTextField(
                value = creditorOrDebtor,
                onValueChange = { viewModel.creditorOrDebtor.value = it },
                label = { Text(if (selectedType == "owed") "Creditor Name" else "Debtor Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { viewModel.amount.value = it },
                label = { Text("Total Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                enabled = !isLoading
            )

            // Remaining Amount
            OutlinedTextField(
                value = remainingAmount,
                onValueChange = { viewModel.remainingAmount.value = it },
                label = { Text("Remaining Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                enabled = !isLoading
            )

            // Due Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = dueDate?.let { dateFormatter.format(it) } ?: "",
                    onValueChange = {},
                    label = { Text("Due Date (Optional)") },
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

            // Is Paid checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isPaid,
                    onCheckedChange = { viewModel.isPaid.value = it },
                    enabled = !isLoading
                )
                Text(
                    text = "Mark as paid",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Error message
            if (uiState is AddDebtUiState.Error) {
                Text(
                    text = (uiState as AddDebtUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Save button
            Button(
                onClick = { viewModel.saveDebt() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && creditorOrDebtor.isNotBlank() && amount.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save")
                }
            }
        }
    }
}

