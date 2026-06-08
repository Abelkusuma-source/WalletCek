package com.app.walletcek.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.walletcek.ui.screens.HomeScreen
import com.app.walletcek.ui.screens.ReportScreen
import com.app.walletcek.ui.screens.SettingsScreen
import com.app.walletcek.ui.screens.AddTransactionScreen
import com.app.walletcek.ui.screens.DebtScreen
import com.app.walletcek.ui.screens.AddDebtScreen
import com.app.walletcek.ui.screens.LoginScreen
import com.app.walletcek.viewmodel.WalletViewModel
import com.app.walletcek.viewmodel.AuthViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun NavGraph(
    navController: NavHostController, 
    viewModel: WalletViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    androidx.compose.runtime.LaunchedEffect(currentUser) {
        if (currentUser != null) {
            viewModel.syncFromCloud()
            // Auto navigate if user is already logged in and we are on login screen
            if (navController.currentDestination?.route == Screen.Login.route) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (currentUser == null) Screen.Login.route else Screen.Home.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { 
                    println("DEBUG: Login Success Triggered")
                    viewModel.syncFromCloud()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(viewModel = viewModel)
        }
        composable(Screen.Report.route) {
            ReportScreen(viewModel = viewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                authViewModel = authViewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Debt.route) {
            DebtScreen(
                viewModel = viewModel,
                onNavigateToAddDebt = { navController.navigate(Screen.AddDebt.route) }
            )
        }
        composable(Screen.AddDebt.route) {
            AddDebtScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
