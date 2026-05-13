package com.app.walletcek.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.walletcek.ui.screens.HomeScreen
import com.app.walletcek.ui.screens.ReportScreen
import com.app.walletcek.ui.screens.SettingsScreen
import com.app.walletcek.ui.screens.AddTransactionScreen
import com.app.walletcek.viewmodel.WalletViewModel

@Composable
fun NavGraph(navController: NavHostController, viewModel: WalletViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(viewModel = viewModel)
        }
        composable(Screen.Report.route) {
            ReportScreen(viewModel = viewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(viewModel = viewModel)
        }
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
