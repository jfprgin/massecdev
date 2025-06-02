package com.example.loginhttp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.loginhttp.InventoryViewModel
import com.example.loginhttp.IssuingGoodsViewModel
import com.example.loginhttp.ReceiptOfGoodsViewModel
import com.example.loginhttp.ReturnOfGoodsViewModel
import com.example.loginhttp.TransferOfGoodsViewModel
import com.example.loginhttp.ui.screens.InventoryScreen
import com.example.loginhttp.ui.screens.IssuingGoodsScreen
import com.example.loginhttp.ui.screens.MenuScreen
import com.example.loginhttp.ui.screens.OrderingGoodsScreen
import com.example.loginhttp.ui.screens.ReceiptOfGoodsScreen
import com.example.loginhttp.ui.screens.ReturnOfGoodsScreen
import com.example.loginhttp.ui.screens.SettingsScreen
import com.example.loginhttp.ui.screens.TransferOfGoodsScreen
import com.example.loginhttp.ui.screens.VirtualWarehouseScreen
import com.example.loginhttp.ui.screens.WarehouseScreen
import com.example.loginhttp.ui.screens.WriteOffOfGoodsScreen

fun NavGraphBuilder.mainNavGraph(
    navHostController: NavHostController,
    rootNavBackStackEntry: NavBackStackEntry?,
    fabContent: MutableState<(@Composable () -> Unit)?>,
    inventoryViewModel: InventoryViewModel,
    receiptOfGoodsViewModel: ReceiptOfGoodsViewModel,
    issuingGoodsViewModel: IssuingGoodsViewModel,
    transferOfGoodsViewModel: TransferOfGoodsViewModel,
    returnOfGoodsViewModel: ReturnOfGoodsViewModel
) {
    navigation(
        startDestination = AppScreen.Main.Home.route,
        route = AppScreen.Main.route,
    ) {
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
            fabContent.value = null
            SettingsScreen(
                onItemClick = {}
            )
        }

        // Warehouse sub-screens
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
                    onClick = { issuingGoodsViewModel.toggleSheet(true) }
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
            WriteOffOfGoodsScreen()
        }
        composable(route = WarehouseRoutes.ORDERING_GOODS) {
//            OrderingGoodsScreen()
        }
        composable(route = WarehouseRoutes.VIRTUAL_WAREHOUSE) {
//            VirtualWarehouseScreen()
        }
        composable(route = WarehouseRoutes.TEMPLATES) {
            // Not yet implemented
        }
    }
}