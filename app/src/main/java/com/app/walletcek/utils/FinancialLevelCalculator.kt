package com.app.walletcek.utils

import androidx.compose.ui.graphics.Color

data class FinancialLevel(
    val title: String,
    val message: String,
    val color: Color,
    val emoji: String,
    val percent: Double
)

object FinancialLevelCalculator {
    fun getFinancialLevel(totalIncome: Double, totalExpense: Double, balance: Double): FinancialLevel {
        val spentPercent = if (totalIncome > 0) (totalExpense / totalIncome) * 100 else if (totalExpense > 0) 100.0 else 0.0
        
        return when {
            balance < 0 -> FinancialLevel(
                title = "Galbay Arc",
                message = "Waktunya gali lubang tutup lubang? 🚨",
                color = Color(0xFFC62828), // Red
                emoji = "🚨",
                percent = spentPercent
            )
            spentPercent >= 100 -> FinancialLevel(
                title = "Gesek Sampai Nol",
                message = "Dompetmu sudah menyerah pada keadaan ☠️",
                color = Color(0xFFC62828), // Red
                emoji = "☠️",
                percent = spentPercent
            )
            spentPercent >= 81 -> FinancialLevel(
                title = "Khilaf Tipis Tipis",
                message = "Mulai makan mie instan akhir bulan 😬",
                color = Color(0xFFEF6C00), // Orange
                emoji = "😬",
                percent = spentPercent
            )
            spentPercent >= 71 -> FinancialLevel(
                title = "Masih Santuy",
                message = "Masih bisa ngopi tanpa panik 😎",
                color = Color(0xFF1976D2), // Blue
                emoji = "😎",
                percent = spentPercent
            )
            else -> FinancialLevel(
                title = "Calon Crazy Rich",
                message = "Tabungan masa depan cerah benderang 🟢",
                color = Color(0xFF2E7D32), // Green
                emoji = "🟢",
                percent = spentPercent
            )
        }
    }
}
