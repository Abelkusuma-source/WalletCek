package com.app.walletcek.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.walletcek.data.entity.DebtEntity
import com.app.walletcek.data.model.DebtStatus

@Composable
fun DebtPaymentDialog(
    debt: DebtEntity,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var paymentAmount by remember { mutableStateOf("") }
    val remainingAmount = debt.amount - debt.paidAmount

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Bayar Utang/Piutang") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Nama: ${debt.personName}")
                Text("Sisa Tagihan: ${formatCurrency(remainingAmount)}")
                
                OutlinedTextField(
                    value = paymentAmount,
                    onValueChange = { paymentAmount = it },
                    label = { Text("Jumlah Bayar") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = paymentAmount.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onConfirm(amount)
                    }
                },
                enabled = paymentAmount.isNotEmpty() && (paymentAmount.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("Bayar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
