package com.abraham.personalfinancemanagementapp.presentation.screens.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abraham.personalfinancemanagementapp.data.service.ExportService
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val biometricEnabled by viewModel.biometricEnabled.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    var showExportDialog by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
        ) {
            // Profile Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Profile picture placeholder
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = currentUser?.name?.takeIf { it.isNotBlank() } 
                                ?: currentUser?.email 
                                ?: "User",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (currentUser?.email != null) {
                            Text(
                                text = currentUser!!.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Settings Options
            SettingsSection(
                title = "Account",
                items = listOf(
                    SettingsItem(
                        title = "Edit Profile",
                        icon = Icons.Default.Edit,
                        onClick = { /* TODO: Navigate to edit profile */ }
                    ),
                    SettingsItem(
                        title = "Change Password",
                        icon = Icons.Default.Lock,
                        onClick = { /* TODO: Navigate to change password */ }
                    )
                )
            )

            SettingsSection(
                title = "Preferences",
                items = listOf(
                    SettingsItem(
                        title = "Currency",
                        icon = Icons.Default.AttachMoney,
                        subtitle = currency,
                        onClick = { /* TODO: Show currency picker */ }
                    ),
                    SettingsItem(
                        title = "Theme",
                        icon = Icons.Default.Palette,
                        subtitle = themeMode.replaceFirstChar { it.uppercase() },
                        onClick = { showThemeDialog = true }
                    ),
                    SettingsItem(
                        title = "Biometric Authentication",
                        icon = Icons.Default.Fingerprint,
                        subtitle = if (biometricEnabled) "Enabled" else "Disabled",
                        onClick = {
                            scope.launch {
                                viewModel.setBiometricEnabled(!biometricEnabled)
                            }
                        }
                    ),
                    SettingsItem(
                        title = "Notifications",
                        icon = Icons.Default.Notifications,
                        subtitle = "Enabled",
                        onClick = { /* TODO: Toggle notifications */ }
                    )
                )
            )

            SettingsSection(
                title = "Data",
                items = listOf(
                    SettingsItem(
                        title = "Export Data",
                        icon = Icons.Default.FileDownload,
                        onClick = { showExportDialog = true }
                    ),
                    SettingsItem(
                        title = "Backup & Sync",
                        icon = Icons.Default.CloudSync,
                        subtitle = "Enabled",
                        onClick = { /* TODO: Toggle sync */ }
                    )
                )
            )

            SettingsSection(
                title = "About",
                items = listOf(
                    SettingsItem(
                        title = "Help & Support",
                        icon = Icons.Default.Help,
                        onClick = { /* TODO: Show help */ }
                    ),
                    SettingsItem(
                        title = "Privacy Policy",
                        icon = Icons.Default.PrivacyTip,
                        onClick = { /* TODO: Show privacy policy */ }
                    ),
                    SettingsItem(
                        title = "Terms of Service",
                        icon = Icons.Default.Description,
                        onClick = { /* TODO: Show terms */ }
                    ),
                    SettingsItem(
                        title = "App Version",
                        icon = Icons.Default.Info,
                        subtitle = "1.0.0",
                        onClick = { }
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
            Button(
                onClick = {
                    scope.launch {
                        viewModel.signOut()
                        onLogout()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // Export Dialog
    if (showExportDialog) {
        ExportDataDialog(
            onDismiss = { showExportDialog = false },
            onExport = { format ->
                showExportDialog = false
                exportData(context, transactions, format, scope) {
                    isExporting = it
                }
            },
            enabled = !isExporting && transactions.isNotEmpty()
        )
    }
    
    if (isExporting) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Exporting...")
                }
            }
        }
    }
    
    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Select Theme") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("system", "light", "dark").forEach { mode ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(mode.replaceFirstChar { it.uppercase() })
                            RadioButton(
                                selected = themeMode == mode,
                                onClick = {
                                    scope.launch {
                                        viewModel.setThemeMode(mode)
                                        showThemeDialog = false
                                    }
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

enum class ExportFormat {
    CSV, EXCEL, PDF
}

@Composable
fun ExportDataDialog(
    onDismiss: () -> Unit,
    onExport: (ExportFormat) -> Unit,
    enabled: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Export Data") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select export format:")
                TextButton(
                    onClick = { onExport(ExportFormat.CSV) },
                    enabled = enabled
                ) {
                    Text("CSV")
                }
                TextButton(
                    onClick = { onExport(ExportFormat.EXCEL) },
                    enabled = enabled
                ) {
                    Text("Excel")
                }
                TextButton(
                    onClick = { onExport(ExportFormat.PDF) },
                    enabled = enabled
                ) {
                    Text("PDF")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun exportData(
    context: Context,
    transactions: List<com.abraham.personalfinancemanagementapp.data.model.Transaction>,
    format: ExportFormat,
    scope: CoroutineScope,
    onExportingChange: (Boolean) -> Unit
) {
    scope.launch {
        onExportingChange(true)
        try {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
            val timestamp = dateFormat.format(Date())
            
            val exportService = ExportService(context)
            val filename = when (format) {
                ExportFormat.CSV -> "transactions_$timestamp.csv"
                ExportFormat.EXCEL -> "transactions_$timestamp.xlsx"
                ExportFormat.PDF -> "transactions_$timestamp.pdf"
            }
            
            val uri = when (format) {
                ExportFormat.CSV -> exportService.exportToCsv(transactions, filename)
                ExportFormat.EXCEL -> exportService.exportToExcel(transactions, filename)
                ExportFormat.PDF -> exportService.exportToPdf(transactions, filename)
            }
            
            // Share the file
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = when (format) {
                    ExportFormat.CSV -> "text/csv"
                    ExportFormat.EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    ExportFormat.PDF -> "application/pdf"
                }
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share file"))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            onExportingChange(false)
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    items: List<SettingsItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        items.forEach { item ->
            SettingsItemRow(item = item)
        }
    }
}

@Composable
fun SettingsItemRow(
    item: SettingsItem,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = item.onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    if (item.subtitle != null) {
                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class SettingsItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val subtitle: String? = null,
    val onClick: () -> Unit
)

