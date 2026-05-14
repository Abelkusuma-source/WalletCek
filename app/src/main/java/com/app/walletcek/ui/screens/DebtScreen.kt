package com.app.walletcek.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.walletcek.data.entity.DebtEntity
import com.app.walletcek.data.model.DebtStatus
import com.app.walletcek.data.model.DebtType
import com.app.walletcek.viewmodel.WalletViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DebtScreen(viewModel: WalletViewModel, onNavigateToAddDebt: () -> Unit) {
    val debts by viewModel.allDebts.collectAsState(initial = emptyList())
    var selectedDebt by remember { mutableStateOf<DebtEntity?>(null) }
    
    val totalDebt = debts.filter { it.type == DebtType.DEBT && it.status == DebtStatus.OPEN }
        .sumOf { it.amount - it.paidAmount }
    val totalReceivable = debts.filter { it.type == DebtType.RECEIVABLE && it.status == DebtStatus.OPEN }
        .sumOf { it.amount - it.paidAmount }

    if (selectedDebt != null) {
        DebtPaymentDialog(
            debt = selectedDebt!!,
            onDismiss = { selectedDebt = null },
            onConfirm = { amount ->
                val updatedPaidAmount = selectedDebt!!.paidAmount + amount
                val newStatus = if (updatedPaidAmount >= selectedDebt!!.amount) DebtStatus.PAID else DebtStatus.OPEN
                viewModel.updateDebt(
                    debt = selectedDebt!!.copy(
                        paidAmount = updatedPaidAmount,
                        status = newStatus
                    ),
                    paymentAmount = amount
                )
                selectedDebt = null
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddDebt) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Utang/Piutang")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Ringkasan Utang",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(
                    title = "Utang Saya",
                    amount = totalDebt,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Piutang",
                    amount = totalReceivable,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Daftar Utang/Piutang",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (debts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada data utang.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(debts) { debt ->
                        DebtItem(
                            debt = debt,
                            onClick = {
                                if (debt.status == DebtStatus.OPEN) {
                                    selectedDebt = debt
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, amount: Double, containerColor: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(
                text = formatCurrency(amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DebtItem(debt: DebtEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (debt.status == DebtStatus.OPEN) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        colors = if (debt.status == DebtStatus.PAID) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = debt.personName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (debt.type == DebtType.DEBT) "Utang" else "Piutang",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (debt.type == DebtType.DEBT) Color.Red else Color.Blue
                )
                if (debt.note.isNotEmpty()) {
                    Text(text = debt.note, style = MaterialTheme.typography.bodySmall)
                }
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                Text(
                    text = "Dibuat: ${dateFormat.format(Date(debt.startDate))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                debt.dueDate?.let {
                    Text(
                        text = "Jatuh Tempo: ${dateFormat.format(Date(it))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (debt.status == DebtStatus.OPEN && it < System.currentTimeMillis()) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatCurrency(debt.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (debt.paidAmount > 0) {
                    Text(
                        text = "Sisa: ${formatCurrency(debt.amount - debt.paidAmount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Text(
                    text = debt.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (debt.status == DebtStatus.PAID) Color.Green else Color.Gray
                )
            }
        }
    }
}

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return format.format(amount)
}
