package com.abraham.personalfinancemanagementapp.presentation.screens.transactions

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.abraham.personalfinancemanagementapp.data.model.Category
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddTransactionUiState
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.AddTransactionViewModel
import com.abraham.personalfinancemanagementapp.util.Constants
import com.abraham.personalfinancemanagementapp.util.ReceiptUploadHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel,
    authRepository: IAuthRepository,
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
    val date by viewModel.date.collectAsStateWithLifecycle()
    val expenseCategories by viewModel.expenseCategories.collectAsStateWithLifecycle()
    val incomeCategories by viewModel.incomeCategories.collectAsStateWithLifecycle()
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploadingReceipt by remember { mutableStateOf(false) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Upload receipt immediately
            scope.launch {
                isUploadingReceipt = true
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    val transactionId = UUID.randomUUID().toString()
                    ReceiptUploadHelper.uploadReceipt(user.id, transactionId, it)
                        .onSuccess { downloadUrl ->
                            viewModel.setReceiptUrl(downloadUrl)
                            isUploadingReceipt = false
                        }
                        .onFailure {
                            isUploadingReceipt = false
                            // Error handling could be added here
                        }
                } else {
                    isUploadingReceipt = false
                }
            }
        }
    }

    val availableCategories = if (selectedType == Constants.TRANSACTION_TYPE_EXPENSE) {
        expenseCategories
    } else {
        incomeCategories
    }

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddTransactionUiState.Success -> {
                onSaveSuccess()
            }
            is AddTransactionUiState.Error -> {
                // Error is shown in UI
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
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
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
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
                    onClick = { viewModel.onTypeChanged(Constants.TRANSACTION_TYPE_EXPENSE) },
                    label = { Text("Expense") },
                    modifier = Modifier.weight(1f)
                )
                
                FilterChip(
                    selected = selectedType == Constants.TRANSACTION_TYPE_INCOME,
                    onClick = { viewModel.onTypeChanged(Constants.TRANSACTION_TYPE_INCOME) },
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

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { viewModel.notes.value = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                enabled = !isLoading
            )

            // Payment Method
            Text(
                text = "Payment Method",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            PaymentMethodSelector(
                selectedMethod = paymentMethod,
                onMethodSelected = { method ->
                    viewModel.paymentMethod.value = method
                }
            )

            // Date
            val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
            OutlinedTextField(
                value = dateFormatter.format(date),
                onValueChange = { },
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { /* TODO: Show date picker */ }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                    }
                },
                enabled = !isLoading
            )

            // Receipt Upload
            Text(
                text = "Receipt (Optional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (selectedImageUri != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Receipt",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = {
                                selectedImageUri = null
                                viewModel.setReceiptUrl("")
                            },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Remove receipt",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            } else {
                OutlinedButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && !isUploadingReceipt
                ) {
                    if (isUploadingReceipt) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Uploading...")
                    } else {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Upload receipt",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload Receipt")
                    }
                }
            }

            // Error message
            if (uiState is AddTransactionUiState.Error) {
                Text(
                    text = (uiState as AddTransactionUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save button
            Button(
                onClick = { viewModel.saveTransaction() },
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

@Composable
fun CategoryChipGroup(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.chunked(3).forEach { rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowCategories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory?.id == category.id,
                        onClick = { onCategorySelected(category) },
                        label = { Text(category.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if row has less than 3 items
                repeat(3 - rowCategories.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun PaymentMethodSelector(
    selectedMethod: String,
    onMethodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PaymentMethodChip(
            label = "Cash",
            value = Constants.PAYMENT_METHOD_CASH,
            selected = selectedMethod == Constants.PAYMENT_METHOD_CASH,
            onClick = { onMethodSelected(Constants.PAYMENT_METHOD_CASH) },
            modifier = Modifier.weight(1f)
        )
        
        PaymentMethodChip(
            label = "Card",
            value = Constants.PAYMENT_METHOD_CARD,
            selected = selectedMethod == Constants.PAYMENT_METHOD_CARD,
            onClick = { onMethodSelected(Constants.PAYMENT_METHOD_CARD) },
            modifier = Modifier.weight(1f)
        )
        
        PaymentMethodChip(
            label = "Bank",
            value = Constants.PAYMENT_METHOD_BANK_TRANSFER,
            selected = selectedMethod == Constants.PAYMENT_METHOD_BANK_TRANSFER,
            onClick = { onMethodSelected(Constants.PAYMENT_METHOD_BANK_TRANSFER) },
            modifier = Modifier.weight(1f)
        )
        
        PaymentMethodChip(
            label = "Digital",
            value = Constants.PAYMENT_METHOD_DIGITAL_WALLET,
            selected = selectedMethod == Constants.PAYMENT_METHOD_DIGITAL_WALLET,
            onClick = { onMethodSelected(Constants.PAYMENT_METHOD_DIGITAL_WALLET) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PaymentMethodChip(
    label: String,
    value: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier
    )
}

