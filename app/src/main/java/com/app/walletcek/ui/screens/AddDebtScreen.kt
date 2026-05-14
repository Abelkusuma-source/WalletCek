package com.app.walletcek.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.walletcek.data.entity.DebtEntity
import com.app.walletcek.data.model.DebtStatus
import com.app.walletcek.data.model.DebtType
import com.app.walletcek.viewmodel.WalletViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDebtScreen(
    viewModel: WalletViewModel,
    onNavigateBack: () -> Unit
) {
    var personName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var debtType by remember { mutableStateOf(DebtType.DEBT) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dueDate = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Utang/Piutang") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = personName,
                onValueChange = { personName = it },
                label = { Text("Nama Orang") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = debtType == DebtType.DEBT,
                    onClick = { debtType = DebtType.DEBT },
                    label = { Text("Utang (Saya Berutang)") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = debtType == DebtType.RECEIVABLE,
                    onClick = { debtType = DebtType.RECEIVABLE },
                    label = { Text("Piutang (Dia Berutang)") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Jumlah") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Catatan") },
                modifier = Modifier.fillMaxWidth()
            )

            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            OutlinedCard(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Jatuh Tempo (Opsional)")
                    Text(
                        text = dueDate?.let { dateFormat.format(Date(it)) } ?: "Pilih Tanggal",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull() ?: 0.0
                    if (personName.isNotEmpty() && amountDouble > 0) {
                        viewModel.insertDebt(
                            DebtEntity(
                                personName = personName,
                                type = debtType,
                                amount = amountDouble,
                                startDate = System.currentTimeMillis(),
                                dueDate = dueDate,
                                note = note,
                                status = DebtStatus.OPEN
                            )
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = personName.isNotEmpty() && amount.isNotEmpty()
            ) {
                Text("Simpan")
            }
        }
    }
}
