package com.example.loginhttp.features.settings.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.loginhttp.features.settings.viewmodel.SettingsViewModel
import com.example.loginhttp.features.settings.screens.CostCentersScreen
import com.example.loginhttp.features.settings.screens.InventoryGroupsScreen
import com.example.loginhttp.features.settings.screens.InventoryListsScreen
import com.example.loginhttp.features.settings.screens.LocationsScreen
import com.example.loginhttp.features.settings.screens.ProductsScreen
import com.example.loginhttp.features.settings.screens.SuppliersScreen
import com.example.loginhttp.features.settings.screens.WarehousesScreen
import com.example.loginhttp.features.settings.viewmodel.CostCentersViewModel
import com.example.loginhttp.features.settings.viewmodel.InventoryGroupsViewModel
import com.example.loginhttp.features.settings.viewmodel.InventoryListsViewModel
import com.example.loginhttp.features.settings.viewmodel.LocationsViewModel
import com.example.loginhttp.features.settings.viewmodel.ProductsViewModel
import com.example.loginhttp.features.settings.viewmodel.SuppliersViewModel
import com.example.loginhttp.features.settings.viewmodel.WarehousesViewModel
import com.example.loginhttp.navigation.AppRoutes
import com.example.loginhttp.navigation.FabAction
import com.example.loginhttp.navigation.SettingsRoutes
import com.example.loginhttp.navigation.UnifiedFloatingActionButton
import com.example.loginhttp.features.settings.screens.SettingsScreen

fun NavGraphBuilder.settingsNavGraph(
    navHostController: NavHostController,
    fabContent: MutableState<(@Composable () -> Unit)?>,
    viewModels: SettingsViewModels
) {
    composable(AppRoutes.SETTINGS) {
        fabContent.value = {
            UnifiedFloatingActionButton(
                icon = Icons.Default.Refresh,
                contentDescription = "Refresh",
                onClick = { viewModels.settingsViewModel.refreshDatabase() }
            )
        }
        SettingsScreen(
            viewModel = viewModels.settingsViewModel,
            onItemClick = navHostController::navigate
        )
    }

    composable(SettingsRoutes.PRODUCTS) {
        fabContent.value = {
            UnifiedFloatingActionButton(
                actions = listOf(
                    FabAction(
                        label = "Preuzmi",
                        icon = Icons.Default.Download,
                        onClick = { viewModels.productsViewModel.downloadItems() }
                    ),
                    FabAction(
                        label = "Učitaj",
                        icon = Icons.Default.Upload,
                        onClick = { viewModels.productsViewModel.loadItems() }
                    ),
                    FabAction(
                        label = "Izvoz",
                        icon = Icons.Default.ImportExport,
                        onClick = { viewModels.productsViewModel.exportItems() }
                    )
                )
            )
        }
        ProductsScreen(viewModel = viewModels.productsViewModel)
    }

    composable(SettingsRoutes.SUPPLIERS) {
        fabContent.value = {
            UnifiedFloatingActionButton(
                icon = Icons.Default.Download,
                contentDescription = "Download",
                onClick = { viewModels.suppliersViewModel.downloadItems() }
            )
        }
        SuppliersScreen(viewModel = viewModels.suppliersViewModel)
    }

    composable(SettingsRoutes.WAREHOUSES) {
        fabContent.value = {
            UnifiedFloatingActionButton(
                icon = Icons.Default.Download,
                contentDescription = "Download",
                onClick = { viewModels.warehousesViewModel.downloadItems() }
            )
        }
        WarehousesScreen(viewModel = viewModels.warehousesViewModel)
    }

    composable(SettingsRoutes.COST_CENTERS) {
        fabContent.value = {
            UnifiedFloatingActionButton(
                icon = Icons.Default.Download,
                contentDescription = "Download",
                onClick = { viewModels.costCentersViewModel.downloadItems() }
            )
        }
        CostCentersScreen(viewModel = viewModels.costCentersViewModel)
    }

    composable(SettingsRoutes.LOCATIONS) {
        fabContent.value = {
            UnifiedFloatingActionButton(
                icon = Icons.Default.Download,
                contentDescription = "Download",
                onClick = { viewModels.locationsViewModel.downloadItems() }
            )
        }
        LocationsScreen(viewModel = viewModels.locationsViewModel)
    }

    composable(SettingsRoutes.INVENTORY_LISTS) {
        fabContent.value = {
            UnifiedFloatingActionButton(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { viewModels.inventoryListsViewModel.toggleSheet(true) }
            )
        }
        InventoryListsScreen(viewModel = viewModels.inventoryListsViewModel)
    }

    composable(SettingsRoutes.INVENTORY_GROUPS) {
        fabContent.value = {
            UnifiedFloatingActionButton(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { viewModels.inventoryGroupsViewModel.toggleSheet(true) }
            )
        }
        InventoryGroupsScreen(viewModel = viewModels.inventoryGroupsViewModel)
    }
}

data class SettingsViewModels(
    val settingsViewModel: SettingsViewModel,
    val productsViewModel: ProductsViewModel,
    val suppliersViewModel: SuppliersViewModel,
    val warehousesViewModel: WarehousesViewModel,
    val costCentersViewModel: CostCentersViewModel,
    val locationsViewModel: LocationsViewModel,
    val inventoryListsViewModel: InventoryListsViewModel,
    val inventoryGroupsViewModel: InventoryGroupsViewModel
)