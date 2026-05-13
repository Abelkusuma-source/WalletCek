package com.app.walletcek.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
