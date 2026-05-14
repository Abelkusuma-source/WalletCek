package com.app.walletcek.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Report : Screen("report", "Report", Icons.Default.Info)
    object Debt : Screen("debt", "Debt", Icons.Default.Money)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object AddTransaction : Screen("add_transaction", "Add Transaction", Icons.Default.Home)
    object AddDebt : Screen("add_debt", "Add Debt", Icons.Default.Money)
}
