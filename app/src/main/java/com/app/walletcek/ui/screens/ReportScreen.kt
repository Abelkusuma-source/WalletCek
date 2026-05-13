package com.app.walletcek.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.walletcek.data.model.TransactionType
import com.app.walletcek.utils.CurrencyUtils
import com.app.walletcek.viewmodel.WalletViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportScreen(viewModel: WalletViewModel) {
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    val categories by viewModel.allCategories.collectAsState(initial = emptyList())

    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    val filteredTransactions = remember(transactions, calendar) {
        transactions.filter {
            val transCal = Calendar.getInstance().apply { timeInMillis = it.date }
            transCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                    transCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
        }
    }

    val totalIncome = filteredTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense = filteredTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    val categoryBreakdown = remember(filteredTransactions, categories) {
        filteredTransactions.groupBy { it.categoryId }.map { (categoryId, trans) ->
            val categoryName = categories.find { it.id == categoryId }?.name ?: "Unknown"
            categoryName to trans.sumOf { it.amount }
        }.sortedByDescending { it.second }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Monthly Report",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Month Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                val newCal = calendar.clone() as Calendar
                newCal.add(Calendar.MONTH, -1)
                calendar = newCal
            }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
            }

            Text(
                text = monthYearFormat.format(calendar.time),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            IconButton(onClick = {
                val newCal = calendar.clone() as Calendar
                newCal.add(Calendar.MONTH, 1)
                calendar = newCal
            }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
            }
        }

        // Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Income", style = MaterialTheme.typography.labelMedium)
                    Text(
                        CurrencyUtils.formatRupiah(totalIncome),
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Expense", style = MaterialTheme.typography.labelMedium)
                    Text(
                        CurrencyUtils.formatRupiah(totalExpense),
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Monthly Balance", fontWeight = FontWeight.Bold)
                Text(
                    CurrencyUtils.formatRupiah(balance),
                    fontWeight = FontWeight.Bold,
                    color = if (balance >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }
        }

        Text(
            text = "Breakdown by Category",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        if (categoryBreakdown.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No data for this month", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(categoryBreakdown) { (name, total) ->
                    ListItem(
                        headlineContent = { Text(name) },
                        trailingContent = { Text(CurrencyUtils.formatRupiah(total), fontWeight = FontWeight.Bold) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
