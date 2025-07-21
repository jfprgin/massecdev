package com.example.loginhttp.ui.menu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.loginhttp.R
import com.example.loginhttp.navigation.SettingsRoutes
import com.example.loginhttp.navigation.WarehouseRoutes

data class MenuItem(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector,
)

val settingsItems = listOf(
    MenuItem(SettingsRoutes.PRODUCTS, R.string.products_title, Icons.Default.ShoppingCart),
    MenuItem(SettingsRoutes.SUPPLIERS, R.string.suppliers_title, Icons.Default.LocalShipping),
    MenuItem(SettingsRoutes.WAREHOUSES, R.string.warehouses_title, Icons.Default.Warehouse),
    MenuItem(SettingsRoutes.COST_CENTERS, R.string.cost_centers_title, Icons.Default.Domain),
    MenuItem(SettingsRoutes.LOCATIONS, R.string.locations_title, Icons.Default.LocationOn),
    MenuItem(SettingsRoutes.INVENTORY_LISTS, R.string.inventory_lists_title, Icons.Default.Assignment),
    MenuItem(SettingsRoutes.INVENTORY_GROUPS, R.string.inventory_groups_title, Icons.Default.Category),
    MenuItem(SettingsRoutes.DIAGNOSTICS, R.string.diagnostics_title, Icons.Default.Code),
)

val warehouseItems = listOf(
    MenuItem(WarehouseRoutes.RECEIPT_OF_GOODS, R.string.receipt_of_goods_title, Icons.Default.MoveToInbox),
    MenuItem(WarehouseRoutes.ISSUING_GOODS, R.string.issuing_goods_title, Icons.Default.Outbox),
    MenuItem(WarehouseRoutes.TRANSFER_OF_GOODS, R.string.transfer_of_goods_title, Icons.Default.Sync),
    MenuItem(WarehouseRoutes.RETURN_OF_GOODS, R.string.return_of_goods_title, Icons.AutoMirrored.Filled.Undo),
    MenuItem(WarehouseRoutes.WRITE_OFF_OF_GOODS, R.string.write_off_of_goods_title, Icons.Default.Delete),
    MenuItem(WarehouseRoutes.ORDERING_GOODS, R.string.ordering_goods_title, Icons.Default.LocalShipping),
    MenuItem(WarehouseRoutes.VIRTUAL_WAREHOUSE, R.string.virtual_warehouse_title, Icons.Default.Warehouse),
    MenuItem(WarehouseRoutes.TEMPLATES, R.string.templates_title, Icons.AutoMirrored.Filled.List)
)