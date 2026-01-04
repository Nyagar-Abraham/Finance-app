package com.abraham.personalfinancemanagementapp.data.service

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.abraham.personalfinancemanagementapp.data.model.Transaction
import com.abraham.personalfinancemanagementapp.util.Constants
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Service for exporting transaction data to various formats
 */
class ExportService(private val context: Context) {

    /**
     * Export transactions to CSV format
     */
    suspend fun exportToCsv(transactions: List<Transaction>, filename: String): Uri = withContext(Dispatchers.IO) {
        val file = File(context.getExternalFilesDir(null), filename)
        FileOutputStream(file).use { fos ->
            // Write CSV header
            fos.write("ID,Date,Type,Category,Amount,Payment Method,Notes,Tags\n".toByteArray())
            
            // Write transaction data
            transactions.forEach { transaction ->
                val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(transaction.date)
                val tagsStr = transaction.tags.joinToString(";")
                val line = "${transaction.id},$dateStr,${transaction.type},${transaction.categoryId}," +
                        "${transaction.amount},${transaction.paymentMethod}," +
                        "\"${transaction.notes.replace("\"", "\"\"")}\",\"$tagsStr\"\n"
                fos.write(line.toByteArray())
            }
        }
        
        getUriForFile(file)
    }

    /**
     * Export transactions to Excel format
     */
    suspend fun exportToExcel(transactions: List<Transaction>, filename: String): Uri = withContext(Dispatchers.IO) {
        val file = File(context.getExternalFilesDir(null), filename)
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Transactions")
        
        // Create header row
        val headerRow = sheet.createRow(0)
        val headers = listOf("ID", "Date", "Type", "Category", "Amount", "Payment Method", "Notes", "Tags")
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            val headerStyle = workbook.createCellStyle()
            val headerFont = workbook.createFont()
            headerFont.bold = true
            headerStyle.setFont(headerFont)
            cell.cellStyle = headerStyle
        }
        
        // Create data rows
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        transactions.forEachIndexed { index, transaction ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(transaction.id)
            row.createCell(1).setCellValue(dateFormat.format(transaction.date))
            row.createCell(2).setCellValue(transaction.type)
            row.createCell(3).setCellValue(transaction.categoryId)
            row.createCell(4).setCellValue(transaction.amount)
            row.createCell(5).setCellValue(transaction.paymentMethod)
            row.createCell(6).setCellValue(transaction.notes)
            row.createCell(7).setCellValue(transaction.tags.joinToString(";"))
        }
        
        // Auto-size columns
        for (i in headers.indices) {
            sheet.autoSizeColumn(i)
        }
        
        FileOutputStream(file).use { fos ->
            workbook.write(fos)
        }
        workbook.close()
        
        getUriForFile(file)
    }

    /**
     * Export transactions to PDF format
     */
    suspend fun exportToPdf(transactions: List<Transaction>, filename: String): Uri = withContext(Dispatchers.IO) {
        val file = File(context.getExternalFilesDir(null), filename)
        val writer = PdfWriter(FileOutputStream(file))
        val pdfDoc = PdfDocument(writer)
        val document = Document(pdfDoc)
        
        // Add title
        val title = Paragraph("Transaction Report")
            .setBold()
            .setFontSize(18f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20f)
        document.add(title)
        
        // Add date info
        val dateInfo = Paragraph("Generated: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}")
            .setFontSize(10f)
            .setMarginBottom(20f)
        document.add(dateInfo)
        
        // Create table
        val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1.5f, 1f, 1f, 1f, 1.5f, 2f, 1f)))
            .useAllAvailableWidth()
        
        // Add header row
        val headers = listOf("ID", "Date", "Type", "Category", "Amount", "Payment Method", "Notes", "Tags")
        headers.forEach { header ->
            val cell = com.itextpdf.layout.element.Cell().add(Paragraph(header))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
            table.addCell(cell)
        }
        
        // Add data rows
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        transactions.forEach { transaction ->
            table.addCell(com.itextpdf.layout.element.Cell().add(Paragraph(transaction.id.take(8))))
            table.addCell(com.itextpdf.layout.element.Cell().add(Paragraph(dateFormat.format(transaction.date))))
            table.addCell(com.itextpdf.layout.element.Cell().add(Paragraph(transaction.type)))
            table.addCell(com.itextpdf.layout.element.Cell().add(Paragraph(transaction.categoryId)))
            table.addCell(com.itextpdf.layout.element.Cell().add(Paragraph(String.format("%.2f", transaction.amount))))
            table.addCell(com.itextpdf.layout.element.Cell().add(Paragraph(transaction.paymentMethod)))
            table.addCell(com.itextpdf.layout.element.Cell().add(Paragraph(transaction.notes.take(30))))
            table.addCell(com.itextpdf.layout.element.Cell().add(Paragraph(transaction.tags.joinToString(", "))))
        }
        
        document.add(table)
        document.close()
        
        getUriForFile(file)
    }

    private fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}

