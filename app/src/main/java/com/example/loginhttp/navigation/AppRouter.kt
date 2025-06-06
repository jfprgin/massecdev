package com.example.loginhttp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import com.example.loginhttp.R

object AppRoutes {
    const val AUTH = "auth"
    const val LOGIN = "login"

    const val MAIN = "main"
    const val HOME = "home"
    const val INVENTORY = "inventory"
    const val WAREHOUSE = "warehouse"
    const val SETTINGS = "settings"

    val mainTabs = listOf(HOME, INVENTORY, WAREHOUSE, SETTINGS)
}

object WarehouseRoutes {
    const val RECEIPT_OF_GOODS = "receipt_of_goods"
    const val ISSUING_GOODS = "issuing_goods"
    const val TRANSFER_OF_GOODS = "transfer_of_goods"
    const val RETURN_OF_GOODS = "return_of_goods"
    const val WRITE_OFF_OF_GOODS = "write_off_of_goods"
    const val ORDERING_GOODS = "ordering_goods"
    const val VIRTUAL_WAREHOUSE = "virtual_warehouse"
    const val TEMPLATES = "templates"

    val all = listOf(
        RECEIPT_OF_GOODS,
        ISSUING_GOODS,
        TRANSFER_OF_GOODS,
        RETURN_OF_GOODS,
        WRITE_OFF_OF_GOODS,
        ORDERING_GOODS,
        VIRTUAL_WAREHOUSE,
        TEMPLATES
    )
}

object SettingsRoutes {
    const val PRODUCTS = "products"
    const val SUPPLIERS = "suppliers"
    const val WAREHOUSES = "warehouses"
    const val COST_CENTERS = "cost_centers"
    const val LOCATIONS = "locations"
    const val INVENTORY_LISTS = "inventory_lists"
    const val INVENTORY_GROUPS = "inventory_groups"

    val all = listOf(
        PRODUCTS,
        SUPPLIERS,
        WAREHOUSES,
        COST_CENTERS,
        LOCATIONS,
        INVENTORY_LISTS,
        INVENTORY_GROUPS
    )
}

// TODO
private object ArgParams {}

sealed class AppScreen(val route: String) {
    object Auth: AppScreen(AppRoutes.AUTH) {
        object Login: AppScreen(AppRoutes.LOGIN)
    }

    object Main: TopLevelDestination(AppRoutes.MAIN) {
        object Home: TopLevelDestination(
            route = AppRoutes.HOME,
            title = R.string.home_title,
            icon = Icons.Default.Home
        )

        object Inventory: TopLevelDestination(
            route = AppRoutes.INVENTORY,
            title = R.string.inventory_title,
            icon = Icons.Default.Inventory
        )

        object Warehouse: TopLevelDestination(
            route = AppRoutes.WAREHOUSE,
            title = R.string.warehouse_title,
            icon = Icons.Default.Warehouse
        )

        object Settings: TopLevelDestination(
            route = AppRoutes.SETTINGS,
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
