package com.app.walletcek.data.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("wallet_cek_prefs", Context.MODE_PRIVATE)

    fun getLastResetMonth(): Int {
        return prefs.getInt("last_reset_month", -1)
    }

    fun setLastResetMonth(month: Int) {
        prefs.edit().putInt("last_reset_month", month).apply()
    }

    fun getThemeMode(): String {
        return prefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM"
    }

    fun setThemeMode(mode: String) {
        prefs.edit().putString("theme_mode", mode).apply()
    }
}
