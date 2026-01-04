package com.abraham.personalfinancemanagementapp.presentation.screens.savings_goals

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
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddSavingsGoalUiState
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddSavingsGoalViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSavingsGoalScreen(
    viewModel: AddSavingsGoalViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val targetAmount by viewModel.targetAmount.collectAsStateWithLifecycle()
    val currentAmount by viewModel.currentAmount.collectAsStateWithLifecycle()
    val deadline by viewModel.deadline.collectAsStateWithLifecycle()
    val icon by viewModel.icon.collectAsStateWithLifecycle()
    val color by viewModel.color.collectAsStateWithLifecycle()

    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddSavingsGoalUiState.Success -> {
                onSaveSuccess()
            }
            is AddSavingsGoalUiState.Error -> {
                // Error is shown in UI
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Savings Goal") },
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
            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.name.value = it },
                label = { Text("Goal Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            // Icon
            OutlinedTextField(
                value = icon,
                onValueChange = { viewModel.icon.value = it },
                label = { Text("Icon (Emoji)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            // Target Amount
            OutlinedTextField(
                value = targetAmount,
                onValueChange = { viewModel.targetAmount.value = it },
                label = { Text("Target Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                enabled = !isLoading
            )

            // Current Amount
            OutlinedTextField(
                value = currentAmount,
                onValueChange = { viewModel.currentAmount.value = it },
                label = { Text("Current Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                enabled = !isLoading
            )

            // Deadline
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = deadline?.let { dateFormatter.format(it) } ?: "",
                    onValueChange = {},
                    label = { Text("Deadline (Optional)") },
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

            // Color
            Text(
                text = "Color",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val colors = listOf(
                    "#4CAF50", "#2196F3", "#FF9800", "#F44336",
                    "#9C27B0", "#00BCD4", "#FFC107", "#795548"
                )
                colors.forEach { colorValue ->
                    FilterChip(
                        selected = color == colorValue,
                        onClick = { viewModel.color.value = colorValue },
                        label = { Text("") },
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Error message
            if (uiState is AddSavingsGoalUiState.Error) {
                Text(
                    text = (uiState as AddSavingsGoalUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Save button
            Button(
                onClick = { viewModel.saveSavingsGoal() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && name.isNotBlank() && targetAmount.isNotBlank()
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

