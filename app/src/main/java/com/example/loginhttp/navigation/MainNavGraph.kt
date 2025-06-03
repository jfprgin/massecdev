package com.example.loginhttp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.loginhttp.InventoryViewModel
import com.example.loginhttp.SettingsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.IssuingGoodsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.OrderingGoodsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.ReceiptOfGoodsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.ReturnOfGoodsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.TransferOfGoodsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.VirtualWarehouseViewModel
import com.example.loginhttp.features.warehouse.viewmodel.WriteOffOfGoodsViewModel
import com.example.loginhttp.ui.screens.InventoryScreen
import com.example.loginhttp.features.warehouse.screens.IssuingGoodsScreen
import com.example.loginhttp.ui.screens.MenuScreen
import com.example.loginhttp.features.warehouse.screens.OrderingGoodsScreen
import com.example.loginhttp.features.warehouse.screens.ReceiptOfGoodsScreen
import com.example.loginhttp.features.warehouse.screens.ReturnOfGoodsScreen
import com.example.loginhttp.ui.screens.SettingsScreen
import com.example.loginhttp.features.warehouse.screens.TransferOfGoodsScreen
import com.example.loginhttp.features.warehouse.screens.VirtualWarehouseScreen
import com.example.loginhttp.ui.screens.WarehouseScreen
import com.example.loginhttp.features.warehouse.screens.WriteOffOfGoodsScreen

fun NavGraphBuilder.mainNavGraph(
    navHostController: NavHostController,
    rootNavBackStackEntry: NavBackStackEntry?,
    fabContent: MutableState<(@Composable () -> Unit)?>,
    inventoryViewModel: InventoryViewModel,
    settingsViewModel: SettingsViewModel,
    receiptOfGoodsViewModel: ReceiptOfGoodsViewModel,
    issuingGoodsViewModel: IssuingGoodsViewModel,
    transferOfGoodsViewModel: TransferOfGoodsViewModel,
    returnOfGoodsViewModel: ReturnOfGoodsViewModel,
    writeOffOfGoodsViewModel: WriteOffOfGoodsViewModel,
    orderingGoodsViewModel: OrderingGoodsViewModel,
    virtualWarehouseViewModel: VirtualWarehouseViewModel
) {
    navigation(
        startDestination = AppScreen.Main.Home.route,
        route = AppScreen.Main.route,
    ) {
        // Main screens (Bottom Navigation)
        composable(route = AppScreen.Main.Home.route) {
            fabContent.value = null
            MenuScreen(             // TODO: Change to HomeScreen
                onMenuClick = {     // TODO: This will probably just change to web view later
                }
            )
        }
        
        composable( route = AppScreen.Main.Inventory.route,) {
            fabContent.value = {
                UnifiedFAB(
                    icon = Icons.Default.Add,
                    contentDescription = "Add",
                    onClick = { inventoryViewModel.toggleSheet(true) }
                )
            }
            InventoryScreen(viewModel = inventoryViewModel)
        }

        composable(route = AppScreen.Main.Warehouse.route,) {
            fabContent.value = null
            WarehouseScreen(onItemClick = navHostController::navigate)
        }

        composable(route = AppScreen.Main.Settings.route,) {
            fabContent.value = {
                UnifiedFAB(
                    Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    onClick = { settingsViewModel.refreshDatabase() }
                )
            }
            SettingsScreen(
                viewModel = settingsViewModel,
                onItemClick = navHostController::navigate
            )
        }

        // Warehouse screens
        warehouseScreens(
            fabContent,
            receiptOfGoodsViewModel,
            issuingGoodsViewModel,
            transferOfGoodsViewModel,
            returnOfGoodsViewModel,
            writeOffOfGoodsViewModel,
            orderingGoodsViewModel,
            virtualWarehouseViewModel
        )
    }
}

private fun NavGraphBuilder.warehouseScreens(
    fabContent: MutableState<@Composable() (() -> Unit)?>,
    receiptOfGoodsViewModel: ReceiptOfGoodsViewModel,
    issuingGoodsViewModel: IssuingGoodsViewModel,
    transferOfGoodsViewModel: TransferOfGoodsViewModel,
    returnOfGoodsViewModel: ReturnOfGoodsViewModel,
    writeOffOfGoodsViewModel: WriteOffOfGoodsViewModel,
    orderingGoodsViewModel: OrderingGoodsViewModel,
    virtualWarehouseViewModel: VirtualWarehouseViewModel
) {
    composable(route = WarehouseRoutes.RECEIPT_OF_GOODS) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { receiptOfGoodsViewModel.toggleSheet(true) }
            )
        }
        ReceiptOfGoodsScreen(viewModel = receiptOfGoodsViewModel)
    }
    composable(route = WarehouseRoutes.ISSUING_GOODS) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { issuingGoodsViewModel.toggleSheet(true) }
            )
        }

        IssuingGoodsScreen(viewModel = issuingGoodsViewModel)
    }
    composable(route = WarehouseRoutes.TRANSFER_OF_GOODS) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { transferOfGoodsViewModel.toggleSheet(true) }
            )
        }

        TransferOfGoodsScreen(viewModel = transferOfGoodsViewModel)
    }
    composable(route = WarehouseRoutes.RETURN_OF_GOODS) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { returnOfGoodsViewModel.toggleSheet(true) }
            )
        }
        ReturnOfGoodsScreen(viewModel = returnOfGoodsViewModel)
    }
    composable(route = WarehouseRoutes.WRITE_OFF_OF_GOODS) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { writeOffOfGoodsViewModel.toggleSheet(true) }
            )
        }
        WriteOffOfGoodsScreen(viewModel = writeOffOfGoodsViewModel)
    }
    composable(route = WarehouseRoutes.ORDERING_GOODS) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { orderingGoodsViewModel.toggleSheet(true) }
            )
        }
        OrderingGoodsScreen(viewModel = orderingGoodsViewModel)
    }
    composable(route = WarehouseRoutes.VIRTUAL_WAREHOUSE) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { virtualWarehouseViewModel.toggleSheet(true) }
            )
        }
        VirtualWarehouseScreen(viewModel = virtualWarehouseViewModel)
    }
    composable(route = WarehouseRoutes.TEMPLATES) {
        // Not yet implemented
    }
}