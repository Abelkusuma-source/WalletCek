package com.app.walletcek.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.walletcek.data.entity.TransactionEntity
import com.app.walletcek.data.model.TransactionType
import com.app.walletcek.utils.CurrencyUtils
import com.app.walletcek.utils.DateUtils
import com.app.walletcek.viewmodel.WalletViewModel
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(viewModel: WalletViewModel) {
    val categories by viewModel.allCategories.collectAsState(initial = emptyList())
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())

    var showDeleteDialog by remember { mutableStateOf<TransactionEntity?>(null) }

    val totalIncome = remember(transactions) {
        transactions.filter { it.type == TransactionType.INCOME && it.categoryId != -1 }.sumOf { it.amount }
    }
    val totalExpense = remember(transactions) {
        transactions.filter { it.type == TransactionType.EXPENSE && it.categoryId != -1 }.sumOf { it.amount }
    }
    val balance = remember(transactions) {
        transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount } -
                transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog?.let { viewModel.deleteTransaction(it) }
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ... (rest of the code stays mostly the same, but TransactionItem will be updated)
        Text(
            text = "Wallet Cek",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Total Balance", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = CurrencyUtils.formatRupiah(balance),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (balance >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    SummaryItem(
                        label = "Income",
                        amount = totalIncome,
                        icon = Icons.Default.ArrowDownward,
                        color = Color(0xFF2E7D32)
                    )
                    SummaryItem(
                        label = "Expense",
                        amount = totalExpense,
                        icon = Icons.Default.ArrowUpward,
                        color = Color(0xFFC62828)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (transactions.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "No transactions yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(transactions) { transaction ->
                    val category = categories.find { it.id == transaction.categoryId }
                    TransactionItem(
                        transaction = transaction,
                        categoryName = category?.name ?: "Unknown",
                        onLongClick = { showDeleteDialog = transaction }
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, amount: Double, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            Text(
                text = CurrencyUtils.formatRupiah(amount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    categoryName: String,
    onLongClick: () -> Unit
) {
    val isDebtTransaction = transaction.categoryId == -1
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(
                if (!isDebtTransaction) {
                    Modifier.combinedClickable(
                        onClick = {},
                        onLongClick = onLongClick
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDebtTransaction) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        ListItem(
            headlineContent = { 
                Text(
                    if (isDebtTransaction) "Hutang/Piutang (LUNAS)" else categoryName,
                    fontWeight = FontWeight.Bold 
                ) 
            },
            supportingContent = { 
                Column {
                    Text(transaction.note)
                    Text(DateUtils.formatDate(transaction.date), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            },
            trailingContent = {
                Text(
                    text = (if (transaction.type == TransactionType.INCOME) "+" else "-") + CurrencyUtils.formatRupiah(transaction.amount),
                    color = if (transaction.type == TransactionType.INCOME) Color(0xFF2E7D32) else Color(0xFFC62828),
                    fontWeight = FontWeight.Bold
                )
            }
        )
    }
}
