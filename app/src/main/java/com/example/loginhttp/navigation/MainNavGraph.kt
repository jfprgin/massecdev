package com.example.loginhttp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.loginhttp.InventoryViewModel
import com.example.loginhttp.ui.screens.InventoryScreen
import com.example.loginhttp.ui.screens.MenuScreen

fun NavGraphBuilder.mainNavGraph(
//    navHostController: NavHostController,
//    rootNavBackStackEntry: NavBackStackEntry?,
    fabContent: MutableState<(@Composable () -> Unit)?>,
    inventoryViewModel: InventoryViewModel,
) {
    // Main screens (Bottom Navigation)
    composable(route = AppScreen.Main.Home.route) {
        fabContent.value = null
        MenuScreen(             // TODO: Change to HomeScreen
            onMenuClick = {     // TODO: This will probably just change to web view later
            }
        )
    }

    composable(route = AppScreen.Main.Inventory.route) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { inventoryViewModel.toggleSheet(true) }
            )
        }
        InventoryScreen(viewModel = inventoryViewModel)
    }

}
