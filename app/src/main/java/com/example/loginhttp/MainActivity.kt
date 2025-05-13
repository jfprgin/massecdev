package com.example.loginhttp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.loginhttp.ui.screens.InventoryScreen
import com.example.loginhttp.ui.screens.LoginScreen
import com.example.loginhttp.ui.screens.MenuScreen
import com.example.loginhttp.ui.screens.SettingsScreen
import com.example.loginhttp.ui.screens.WarehouseScreen
import com.example.loginhttp.ui.theme.LoginHTTPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginHTTPTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Routes.LOGIN,
                ) {
                    composable(Routes.LOGIN) {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Routes.MENU) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Routes.MENU) {
                        MenuScreen(
                            onMenuClick = { route ->
                                navController.navigate(route)
                            }
                        )
                    }

                    composable(Routes.INVENTORY) {
                        InventoryScreen(
                            selectedScreen = Routes.INVENTORY,
                            onNavigate = {
                                navController.navigate(it)
                            }
                        )
                    }

                    composable(Routes.WAREHOUSE) {
                        WarehouseScreen(
                            selectedScreen = Routes.WAREHOUSE,
                            onNavigate = {
                                navController.navigate(it)
                            },
                            onItemClick = {}
                        )
                    }

                    composable(Routes.SETTINGS) {
                        SettingsScreen(
                            selectedScreen = Routes.SETTINGS,
                            onNavigate = { navController.navigate(it) },
                            onItemClick = {}
                        )
                    }
                }

//                val loginViewModel: LoginViewModel = viewModel()
//
//                LoginScreen(viewModel = loginViewModel)
            }
        }
    }
}

object Routes {
    const val LOGIN = "Login"
    const val MENU = "Menu"
    const val INVENTORY = "Inventory"
    const val WAREHOUSE = "Warehouse"
    const val SETTINGS = "Settings"
}