package com.example.loginhttp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import com.example.loginhttp.R

private object Routes {
    const val AUTH = "auth"
    const val LOGIN = "login"

    const val MAIN = "main"
    const val HOME = "home"
    const val INVENTORY = "inventory"
    const val WAREHOUSE = "warehouse"
    const val SETTINGS = "settings"
}

// TODO
private object ArgParams {}

sealed class AppScreen(val route: String) {
    object Auth: AppScreen(Routes.AUTH) {
        object Login: AppScreen(Routes.LOGIN)
    }

    object Main: TopLevelDestination(Routes.MAIN) {
        object Home: TopLevelDestination(
            route = Routes.HOME,
            title = R.string.home_title,
            icon = Icons.Default.Home
        )

        object Inventory: TopLevelDestination(
            route = Routes.INVENTORY,
            title = R.string.inventory_title,
            icon = Icons.Default.Inventory
        )

        object Warehouse: TopLevelDestination(
            route = Routes.WAREHOUSE,
            title = R.string.warehouse_title,
            icon = Icons.Default.Warehouse
        )

        object Settings: TopLevelDestination(
            route = Routes.SETTINGS,
            title =R.string.settings_title,
            icon = Icons.Default.Settings
        )
    }
}

sealed class TopLevelDestination(
    val route: String,
    val title: Int? = null,
    val icon: ImageVector? = null,
    val navArguments: List<NamedNavArgument> = emptyList(),
)
