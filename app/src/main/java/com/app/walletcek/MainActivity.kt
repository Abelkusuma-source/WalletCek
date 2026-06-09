package com.app.walletcek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.walletcek.ui.navigation.NavGraph
import com.app.walletcek.ui.navigation.Screen
import com.app.walletcek.ui.theme.WalletCekTheme
import com.app.walletcek.viewmodel.WalletViewModel
import com.app.walletcek.viewmodel.WalletViewModelFactory
import com.app.walletcek.viewmodel.AuthViewModel
import com.app.walletcek.viewmodel.AuthViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.app.walletcek.data.utils.BackupManager
import com.app.walletcek.data.utils.PreferenceManager
import android.content.Intent
import android.net.Uri
import com.app.walletcek.utils.FileProcessingUtils
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect

class MainActivity : ComponentActivity() {
    private lateinit var walletViewModel: WalletViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            walletViewModel = viewModel(
                factory = WalletViewModelFactory(
                    (application as WalletApplication).repository,
                    PreferenceManager(applicationContext),
                    BackupManager(applicationContext)
                )
            )
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory((application as WalletApplication).authRepository)
            )
            
            // Handle shared intent if any
            LaunchedEffect(Unit) {
                handleIntent(intent)
            }

            val themeMode by walletViewModel.themeMode
            val darkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            WalletCekTheme(darkTheme = darkTheme) {
                MainScreen(walletViewModel, authViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND) {
            val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            if (uri != null) {
                lifecycleScope.launch {
                    val extractedText = FileProcessingUtils.extractTextFromUri(this@MainActivity, uri)
                    if (extractedText.isNotEmpty()) {
                        walletViewModel.sharedText.value = extractedText
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: WalletViewModel, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentUser by authViewModel.currentUser.collectAsState()
    
    val sharedText by viewModel.sharedText

    LaunchedEffect(sharedText) {
        if (sharedText != null) {
            navController.navigate(Screen.AddTransaction.route)
        }
    }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            viewModel.syncFromCloud()
        }
    }

    val screens = listOf(
        Screen.Home,
        Screen.Report,
        Screen.Debt,
        Screen.Settings
    )

    val showBottomBar = screens.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    screens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentDestination?.route == Screen.Home.route) {
                FloatingActionButton(onClick = { navController.navigate(Screen.AddTransaction.route) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(
                navController = navController, 
                viewModel = viewModel,
                authViewModel = authViewModel
            )
        }
    }
}
