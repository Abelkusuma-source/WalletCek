package com.app.walletcek.utils

import java.text.NumberFormat
import java.util.*

object CurrencyUtils {
    fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount)
    }
}
