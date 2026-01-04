package com.abraham.personalfinancemanagementapp.presentation.screens.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddCategoryUiState
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddCategoryViewModel
import com.abraham.personalfinancemanagementapp.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    viewModel: AddCategoryViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val icon by viewModel.icon.collectAsStateWithLifecycle()
    val color by viewModel.color.collectAsStateWithLifecycle()

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddCategoryUiState.Success -> {
                onSaveSuccess()
            }
            is AddCategoryUiState.Error -> {
                // Error is shown in UI
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Category") },
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

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.name.value = it },
                label = { Text("Category Name") },
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
            if (uiState is AddCategoryUiState.Error) {
                Text(
                    text = (uiState as AddCategoryUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Save button
            Button(
                onClick = { viewModel.saveCategory() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading && name.isNotBlank()
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

