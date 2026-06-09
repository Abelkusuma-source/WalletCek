package com.app.walletcek.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.content.Intent
import com.app.walletcek.data.entity.CategoryEntity
import com.app.walletcek.data.entity.TransactionEntity
import com.app.walletcek.data.model.TransactionType
import com.app.walletcek.ui.ocr.ReceiptScannerActivity
import com.app.walletcek.viewmodel.WalletViewModel
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: WalletViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }

    // Helper function to format input with dots
    fun formatDisplayAmount(input: String): String {
        val clean = input.replace(".", "").replace(",", "")
        val parsed = clean.toLongOrNull() ?: return ""
        return String.format(Locale("id", "ID"), "%,d", parsed).replace(",", ".")
    }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scannedText = result.data?.getStringExtra("scanned_text") ?: ""
            if (scannedText.isNotEmpty()) {
                val parsedAmount = parseReceipt(scannedText)
                if (parsedAmount > 0) {
                    amount = formatDisplayAmount(parsedAmount.toLong().toString())
                    note = scannedText.take(300)
                }
            }
        }
    }

    val categories by viewModel.getCategoriesByType(selectedType).collectAsState(initial = emptyList())

    val sharedText by viewModel.sharedText

    LaunchedEffect(sharedText) {
        sharedText?.let { text ->
            if (text.isNotEmpty()) {
                val parsedAmount = parseReceipt(text)
                if (parsedAmount > 0) {
                    amount = formatDisplayAmount(parsedAmount.toLong().toString())
                    note = text.take(300)
                }
                viewModel.clearSharedText()
            }
        }
    }

    // Reset category when type changes
    LaunchedEffect(selectedType) {
        selectedCategory = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Type Selector (Income/Expense)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedType == TransactionType.EXPENSE,
                    onClick = { selectedType = TransactionType.EXPENSE },
                    label = { Text("Expense") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedType == TransactionType.INCOME,
                    onClick = { selectedType = TransactionType.INCOME },
                    label = { Text("Income") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Scan Receipt Button
            OutlinedButton(
                onClick = { 
                    val intent = Intent(context, ReceiptScannerActivity::class.java)
                    scannerLauncher.launch(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.DocumentScanner, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan Receipt")
            }

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { input ->
                    val clean = input.replace(".", "").replace(",", "")
                    if (clean.all { it.isDigit() }) {
                        amount = if (clean.isEmpty()) "" else formatDisplayAmount(clean)
                    }
                },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("Rp ") }
            )

            // Category Selection (Scrollable)
            Text("Category", style = MaterialTheme.typography.labelLarge)
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(categories.size) { index ->
                    val category = categories[index]
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category.name) }
                    )
                }
            }

            // Note Input
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = {
                    val cleanAmount = amount.replace(".", "").replace(",", "")
                    val amountDouble = cleanAmount.toDoubleOrNull()
                    if (amountDouble == null || amountDouble <= 0) {
                        scope.launch { snackbarHostState.showSnackbar("Please enter a valid amount") }
                        return@Button
                    }
                    if (selectedCategory == null) {
                        scope.launch { snackbarHostState.showSnackbar("Please select a category") }
                        return@Button
                    }

                    val transaction = TransactionEntity(
                        amount = amountDouble,
                        note = note,
                        date = System.currentTimeMillis(),
                        type = selectedType,
                        categoryId = selectedCategory!!.id
                    )
                    viewModel.insertTransaction(transaction)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Transaction")
            }
        }
    }
}

private fun parseReceipt(text: String): Double {
    val lines = text.split("\n")
    var potentialAmounts = mutableListOf<Double>()
    
    // Regex untuk menangkap angka (mendukung format 10.000 atau 10,000 atau 10000)
    val amountPattern = Pattern.compile("(\\d{1,3}(?:[.,]\\d{3})+|\\d{2,})")
    
    for (line in lines) {
        val upperLine = line.uppercase()
        
        // Kata kunci yang biasanya merujuk pada total belanja di struk Indonesia/Inggris
        val isTotalLine = upperLine.contains("TOTAL") || 
                         upperLine.contains("AMT") || 
                         upperLine.contains("HARGA") || 
                         upperLine.contains("BAYAR") || 
                         upperLine.contains("AMOUNT") ||
                         upperLine.contains("DUE")

        if (isTotalLine) {
            val matcher = amountPattern.matcher(line)
            while (matcher.find()) {
                val cleanAmount = matcher.group(1)
                    ?.replace(".", "")
                    ?.replace(",", "")
                    ?.toDoubleOrNull()
                
                if (cleanAmount != null && cleanAmount > 100) {
                    potentialAmounts.add(cleanAmount)
                }
            }
        }
    }
    
    // Jika tidak ada baris dengan kata kunci, cari angka terbesar di seluruh teks
    if (potentialAmounts.isEmpty()) {
        val matcher = amountPattern.matcher(text)
        while (matcher.find()) {
            val cleanAmount = matcher.group(1)
                ?.replace(".", "")
                ?.replace(",", "")
                ?.toDoubleOrNull()
            
            // Filter: Abaikan angka yang terlalu kecil (bukan harga) 
            // atau terlalu besar (mungkin nomor telepon/no struk)
            if (cleanAmount != null && cleanAmount > 100 && cleanAmount < 100000000) {
                potentialAmounts.add(cleanAmount)
            }
        }
    }
    
    // Ambil angka terbesar karena TOTAL biasanya angka paling besar di struk
    return potentialAmounts.maxOrNull() ?: 0.0
}
