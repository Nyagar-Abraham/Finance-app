package com.abraham.personalfinancemanagementapp.presentation.screens.reports

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abraham.personalfinancemanagementapp.data.service.ExportService
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.BudgetWithProgress
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.MonthlyData
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.ReportPeriod
import com.abraham.personalfinancemanagementapp.presentation.viewmodel.ReportsViewModel
import com.abraham.personalfinancemanagementapp.ui.theme.PrimaryBlue
import com.abraham.personalfinancemanagementapp.ui.theme.PrimaryPurple
// Vico chart imports - TODO: Fix imports once correct Vico 2.0.0-alpha.28 API structure is confirmed
// The alpha version may have a different package structure
// import com.patrykandpatrick.vico.compose.Chart
// import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
// import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
// import com.patrykandpatrick.vico.compose.chart.column.columnChart
// import com.patrykandpatrick.vico.compose.chart.line.lineChart
// import com.patrykandpatrick.vico.compose.chart.pie.pieChart
// import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
// import com.patrykandpatrick.vico.core.entry.entryModelOf
// import com.patrykandpatrick.vico.core.model.columnSeries
// import com.patrykandpatrick.vico.core.model.lineSeries
// import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onBackClick: () -> Unit,
    onAnalyticsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()
    val netBalance by viewModel.netBalance.collectAsStateWithLifecycle()
    val expenseByCategory by viewModel.expenseByCategory.collectAsStateWithLifecycle()
    val incomeByCategory by viewModel.incomeByCategory.collectAsStateWithLifecycle()
    val monthlyData by viewModel.monthlyData.collectAsStateWithLifecycle()
    val budgetsWithSpending by viewModel.budgetsWithSpending.collectAsStateWithLifecycle()
    val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showExportDialog by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-KE"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports & Analytics") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onAnalyticsClick
                    ) {
                        Icon(Icons.Default.BarChart, contentDescription = "Analytics")
                    }
                    IconButton(
                        onClick = { showExportDialog = true },
                        enabled = !isLoading && transactions.isNotEmpty()
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export Data")
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
            // Period selector
            PeriodSelector(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { period ->
                    viewModel.setPeriod(period)
                }
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Summary cards
                SummaryCard(
                    title = "Total Income",
                    amount = totalIncome,
                    currencyFormatter = currencyFormatter,
                    color = Color(0xFF10B981)
                )

                SummaryCard(
                    title = "Total Expense",
                    amount = totalExpense,
                    currencyFormatter = currencyFormatter,
                    color = MaterialTheme.colorScheme.error
                )

                SummaryCard(
                    title = "Net Balance",
                    amount = netBalance,
                    currencyFormatter = currencyFormatter,
                    color = if (netBalance >= 0) PrimaryBlue else MaterialTheme.colorScheme.error
                )

                // Spending Breakdown Pie Chart
                if (expenseByCategory.isNotEmpty()) {
                    Text(
                        text = "Expenses by Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    ExpenseBreakdownPieChart(
                        expenseByCategory = expenseByCategory,
                        getCategoryName = { viewModel.getCategoryName(it) },
                        currencyFormatter = currencyFormatter
                    )
                }

                // Income vs Expense Bar/Line Chart
                if (monthlyData.isNotEmpty()) {
                    Text(
                        text = "Monthly Trends",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    IncomeExpenseChart(
                        monthlyData = monthlyData.takeLast(6),
                        currencyFormatter = currencyFormatter
                    )
                }

                // Budget Progress Bars
                if (budgetsWithSpending.isNotEmpty()) {
                    Text(
                        text = "Budget Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    budgetsWithSpending.forEach { budgetWithProgress ->
                        BudgetProgressCard(
                            budgetWithProgress = budgetWithProgress,
                            getCategoryName = { viewModel.getCategoryName(it) },
                            currencyFormatter = currencyFormatter
                        )
                    }
                }

                if (expenseByCategory.isEmpty() && incomeByCategory.isEmpty() && monthlyData.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No data available for the selected period",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Data") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select export format:")
                    TextButton(
                        onClick = {
                            showExportDialog = false
                            exportData(context, viewModel, transactions, ExportFormat.CSV, scope) {
                                isExporting = it
                            }
                        },
                        enabled = !isExporting
                    ) {
                        Text("CSV")
                    }
                    TextButton(
                        onClick = {
                            showExportDialog = false
                            exportData(context, viewModel, transactions, ExportFormat.EXCEL, scope) {
                                isExporting = it
                            }
                        },
                        enabled = !isExporting
                    ) {
                        Text("Excel")
                    }
                    TextButton(
                        onClick = {
                            showExportDialog = false
                            exportData(context, viewModel, transactions, ExportFormat.PDF, scope) {
                                isExporting = it
                            }
                        },
                        enabled = !isExporting
                    ) {
                        Text("PDF")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Cancel")
                }
            }
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
}

enum class ExportFormat {
    CSV, EXCEL, PDF
}

private fun exportData(
    context: Context,
    viewModel: ReportsViewModel,
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
fun ExpenseBreakdownPieChart(
    expenseByCategory: Map<String, Double>,
    getCategoryName: (String) -> String,
    currencyFormatter: NumberFormat,
    modifier: Modifier = Modifier
) {
    if (expenseByCategory.isEmpty()) return
    
    val sortedEntries = expenseByCategory.entries.sortedByDescending { it.value }
    val colors = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFF8B5CF6), // Purple
        Color(0xFFEC4899), // Pink
        Color(0xFFF59E0B), // Amber
        Color(0xFF10B981), // Green
        Color(0xFF3B82F6), // Blue
        Color(0xFFEF4444), // Red
        Color(0xFF14B8A6), // Teal
    )
    
    val entries = sortedEntries.mapIndexed { index, entry ->
        entry.value to colors.getOrElse(index) { Color.Gray }
    }
    
    val total = expenseByCategory.values.sum()
    
    // TODO: Replace with Vico pie chart once correct imports are confirmed for 2.0.0-alpha.28
    // For now, using progress bars as visualization
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Spending Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Visual representation using progress bars
            sortedEntries.take(8).forEachIndexed { index, entry ->
                val percentage = (entry.value / total * 100).toFloat()
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = getCategoryName(entry.key).ifBlank { "Uncategorized" },
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${String.format("%.1f", percentage)}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    LinearProgressIndicator(
                        progress = { (percentage / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = colors.getOrElse(index) { Color.Gray },
                        trackColor = colors.getOrElse(index) { Color.Gray }.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun IncomeExpenseChart(
    monthlyData: List<MonthlyData>,
    currencyFormatter: NumberFormat,
    modifier: Modifier = Modifier
) {
    if (monthlyData.isEmpty()) return
    
    val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    
    val labels = monthlyData.map { "${monthNames[it.month - 1]} ${it.year % 100}" }
    val incomeData = monthlyData.map { it.income.toFloat() }
    val expenseData = monthlyData.map { it.expense.toFloat() }
    
    val maxValue = maxOf(
        incomeData.maxOrNull() ?: 0f,
        expenseData.maxOrNull() ?: 0f
    )
    
    // TODO: Replace with Vico bar/line chart once correct imports are confirmed for 2.0.0-alpha.28
    // For now, using card-based visualization
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Monthly Income vs Expense",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Visual representation using bars
            monthlyData.forEachIndexed { index, data ->
                val monthLabel = labels.getOrNull(index) ?: ""
                val incomePercentage = if (maxValue > 0) (data.income.toFloat() / maxValue * 100f) else 0f
                val expensePercentage = if (maxValue > 0) (data.expense.toFloat() / maxValue * 100f) else 0f
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = monthLabel,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    
                    // Income bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Income",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(60.dp)
                        )
                        LinearProgressIndicator(
                            progress = { (incomePercentage / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .weight(1f)
                                .height(16.dp),
                            color = Color(0xFF10B981),
                            trackColor = Color(0xFF10B981).copy(alpha = 0.2f)
                        )
                        Text(
                            text = currencyFormatter.format(data.income),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(80.dp),
                            textAlign = TextAlign.End
                        )
                    }
                    
                    // Expense bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Expense",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(60.dp)
                        )
                        LinearProgressIndicator(
                            progress = { (expensePercentage / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .weight(1f)
                                .height(16.dp),
                            color = MaterialTheme.colorScheme.error,
                            trackColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                        )
                        Text(
                            text = currencyFormatter.format(data.expense),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(80.dp),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
            
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                Color(0xFF10B981),
                                RoundedCornerShape(4.dp)
                            )
                    )
                    Text("Income", style = MaterialTheme.typography.bodySmall)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                MaterialTheme.colorScheme.error,
                                RoundedCornerShape(4.dp)
                            )
                    )
                    Text("Expense", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun BudgetProgressCard(
    budgetWithProgress: BudgetWithProgress,
    getCategoryName: (String) -> String,
    currencyFormatter: NumberFormat,
    modifier: Modifier = Modifier
) {
    val budget = budgetWithProgress.budget
    val color = when {
        budgetWithProgress.isOverBudget -> MaterialTheme.colorScheme.error
        budgetWithProgress.isWarning -> Color(0xFFFF9800) // Orange
        else -> Color(0xFF10B981) // Green
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = getCategoryName(budget.categoryId).ifBlank { "Uncategorized" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LinearProgressIndicator(
                progress = budgetWithProgress.progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )

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
                        text = currencyFormatter.format(budgetWithProgress.spent),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }

            if (budgetWithProgress.remaining >= 0) {
                Text(
                    text = "${currencyFormatter.format(budgetWithProgress.remaining)} remaining",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF10B981)
                )
            } else {
                Text(
                    text = "${currencyFormatter.format(-budgetWithProgress.remaining)} over budget",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun PeriodSelector(
    selectedPeriod: ReportPeriod,
    onPeriodSelected: (ReportPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReportPeriod.values().forEach { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = { Text(period.name.lowercase().replaceFirstChar { it.uppercase() }) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    currencyFormatter: NumberFormat,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(color.copy(alpha = 0.8f), color)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currencyFormatter.format(amount),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

