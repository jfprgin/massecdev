package com.example.loginhttp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.loginhttp.ui.components.BottomNavBar
import com.example.loginhttp.ui.screens.InventoryScreen
import com.example.loginhttp.ui.screens.LoginScreen
import com.example.loginhttp.ui.screens.MenuScreen
import com.example.loginhttp.ui.screens.SettingsScreen
import com.example.loginhttp.ui.screens.WarehouseScreen
import com.example.loginhttp.ui.theme.LoginHTTPTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginHTTPTheme {

                val rootNavController = rememberNavController()
                val navBackStackEntry by rootNavController.currentBackStackEntryAsState()

                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (currentRoute != Routes.LOGIN) {
                            BottomNavBar(
                                selectedScreen = currentRoute ?: "",
                                onNavigate = {
                                    rootNavController.navigate(it) {
                                        popUpTo(rootNavController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) {
                    NavHost(
                        navController = rootNavController,
                        startDestination = Routes.LOGIN,
                    ) {
                        composable(Routes.LOGIN) {
                            LoginScreen(
                                onLoginSuccess = {
                                    rootNavController.navigate(Routes.HOME) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Routes.HOME) {
                            MenuNavHost()
                        }
                        composable(Routes.INVENTORY) {
                            InventoryNavHost()
                        }
                        composable(Routes.WAREHOUSE) {
                            WarehouseNavHost()
                        }
                        composable(Routes.SETTINGS) {
                            SettingsNavHost()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuNavHost() {
    val menuNavController = rememberNavController()
    NavHost(
        navController = menuNavController,
        startDestination = Routes.HOME,
    ) {
        composable(Routes.HOME) {
            MenuScreen(
                onMenuClick = {
                }
            )
        }
    }
}

@Composable
fun InventoryNavHost() {
    val inventoryNavController = rememberNavController()
    NavHost(
        navController = inventoryNavController,
        startDestination = Routes.INVENTORY,
    ) {
        composable(Routes.INVENTORY) {
            InventoryScreen(
                selectedScreen = Routes.INVENTORY,
                onNavigate = {
                    inventoryNavController.navigate(it)
                },
            )
        }
    }
}

@Composable
fun WarehouseNavHost() {
    val warehouseNavController = rememberNavController()
    NavHost(
        navController = warehouseNavController,
        startDestination = Routes.WAREHOUSE,
    ) {
        composable(Routes.WAREHOUSE) {
            WarehouseScreen(
                selectedScreen = Routes.WAREHOUSE,
                onNavigate = {
                    warehouseNavController.navigate(it)
                },
                onItemClick = {}
            )
        }
    }
}

@Composable
fun SettingsNavHost() {
    val settingsNavController = rememberNavController()
    NavHost(
        navController = settingsNavController,
        startDestination = Routes.SETTINGS,
    ) {
        composable(Routes.SETTINGS) {
            SettingsScreen(
                selectedScreen = Routes.SETTINGS,
                onNavigate = { settingsNavController.navigate(it) },
                onItemClick = {}
            )
        }
    }
}

object Routes {
    const val LOGIN = "Login"
    const val HOME = "Home"
    const val INVENTORY = "Inventory"
    const val WAREHOUSE = "Warehouse"
    const val SETTINGS = "Settings"
}