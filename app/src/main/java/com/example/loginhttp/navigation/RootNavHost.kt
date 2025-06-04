package com.example.loginhttp.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.loginhttp.InventoryViewModel
import com.example.loginhttp.features.warehouse.viewmodel.IssuingGoodsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.OrderingGoodsViewModel
import com.example.loginhttp.R
import com.example.loginhttp.SettingsViewModel
import com.example.loginhttp.features.settings.viewmodel.CostCentersViewModel
import com.example.loginhttp.features.settings.viewmodel.InventoryGroupsViewModel
import com.example.loginhttp.features.settings.viewmodel.InventoryListsViewModel
import com.example.loginhttp.features.settings.viewmodel.LocationsViewModel
import com.example.loginhttp.features.settings.viewmodel.ProductsViewModel
import com.example.loginhttp.features.settings.viewmodel.SuppliersViewModel
import com.example.loginhttp.features.settings.viewmodel.WarehousesViewModel
import com.example.loginhttp.features.warehouse.viewmodel.ReceiptOfGoodsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.ReturnOfGoodsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.TransferOfGoodsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.VirtualWarehouseViewModel
import com.example.loginhttp.features.warehouse.viewmodel.WriteOffOfGoodsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RootNavHost(isAuthenticated: Boolean) {
    SharedTransitionLayout {
        val topAppbarTitle = remember { mutableStateOf("") }
//        val topAppBarState = rememberTopAppBarState()
//        val barScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(state = topAppBarState)

        val showTopBarState = rememberSaveable { (mutableStateOf(true)) }
        val showBottomBarState = rememberSaveable { (mutableStateOf(true)) }

        val fabContent = remember { mutableStateOf<@Composable (() -> Unit)?>(null) }

//        val coroutineScope = rememberCoroutineScope()
        val rootNavHostController = rememberNavController()
        val rootNavBackStackEntry by rootNavHostController.currentBackStackEntryAsState()

        val inventoryViewModel: InventoryViewModel = viewModel()
        val settingsViewModel: SettingsViewModel = viewModel()

        // Warehouse screens ViewModels
        val receiptOfGoodsViewModel: ReceiptOfGoodsViewModel = viewModel()
        val issuingGoodsViewModel: IssuingGoodsViewModel = viewModel()
        val transferOfGoodsViewModel: TransferOfGoodsViewModel = viewModel()
        val returnOfGoodsViewModel: ReturnOfGoodsViewModel = viewModel()
        val writeOffOfGoodsViewModel: WriteOffOfGoodsViewModel = viewModel()
        val orderingGoodsViewModel: OrderingGoodsViewModel = viewModel()
        val virtualWarehouseViewModel: VirtualWarehouseViewModel = viewModel()

        // Settings screens ViewModels
        val productsViewModel: ProductsViewModel = viewModel()
        val suppliersViewModel: SuppliersViewModel = viewModel()
        val warehousesViewModel: WarehousesViewModel = viewModel()
        val costCentersViewModel: CostCentersViewModel = viewModel()
        val locationsViewModel: LocationsViewModel = viewModel()
        val inventoryListsViewModel: InventoryListsViewModel = viewModel()
        val inventoryGroupsViewModel: InventoryGroupsViewModel = viewModel()

        // Control TopBar and BottomBar
        when (rootNavBackStackEntry?.destination?.route) {
            AppScreen.Main.Home.route -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(AppScreen.Main.Home.title!!)
            }

            AppScreen.Main.Inventory.route -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(AppScreen.Main.Inventory.title!!)
            }

            AppScreen.Main.Warehouse.route -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(AppScreen.Main.Warehouse.title!!)
            }

            AppScreen.Main.Settings.route -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(AppScreen.Main.Settings.title!!)
            }

            // Warehouse screens
            WarehouseRoutes.RECEIPT_OF_GOODS -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(R.string.receipt_of_goods_title)
            }

            WarehouseRoutes.ISSUING_GOODS -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(R.string.issuing_goods_title)
            }

            WarehouseRoutes.TRANSFER_OF_GOODS -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(R.string.transfer_of_goods_title)
            }

            WarehouseRoutes.RETURN_OF_GOODS -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(R.string.return_of_goods_title)
            }

            WarehouseRoutes.WRITE_OFF_OF_GOODS -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(R.string.write_off_goods_title)
            }

            WarehouseRoutes.ORDERING_GOODS -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(R.string.ordering_goods_title)
            }

            WarehouseRoutes.VIRTUAL_WAREHOUSE -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(R.string.virtual_warehouse_title)
            }

            // Settings screens
            SettingsRoutes.PRODUCTS -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(R.string.products_title)
            }

            SettingsRoutes.SUPPLIERS -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(R.string.suppliers_title)
            }

            SettingsRoutes.WAREHOUSES -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(R.string.warehouses_title)
            }

            SettingsRoutes.COST_CENTERS -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(R.string.cost_centers_title)
            }

            SettingsRoutes.LOCATIONS -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(R.string.locations_title)
            }

            SettingsRoutes.INVENTORY_LISTS -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(R.string.inventory_lists_title)
            }

            SettingsRoutes.INVENTORY_GROUPS -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(R.string.inventory_groups_title)
            }

            else -> {
                showBottomBarState.value = false
                showTopBarState.value = false
            }
        }

        Scaffold(
            // TODO: topbar
            topBar = {
                if (showTopBarState.value) {
//                    AppTopBar(
//                        topAppbarTitle.value,
//                        barScrollBehavior,
//                    )
                }
            },
            bottomBar = {
                if (showBottomBarState.value) {
                    BottomNavBar(navController = rootNavHostController)
                }
            },
            floatingActionButton = fabContent.value ?: {},
        ) {
            Column(
                modifier = Modifier
                    // Padding should be equal to the bottom bar height
                    .padding(bottom = 56.dp)
            ) {
                NavHost(
                    navController = rootNavHostController,
                    startDestination = if (isAuthenticated) AppScreen.Main.route else AppScreen.Auth.route,
                    enterTransition = {
                        EnterTransition.None
                    },
                    exitTransition = {
                        ExitTransition.None
                    }
                ) {
                    authNavGraph(
                        rootNavHostController
                    )
                    mainNavGraph(
                        rootNavHostController,
                        rootNavBackStackEntry,

                        fabContent = fabContent,
                        inventoryViewModel,
                        settingsViewModel,

                        // Warehouse screens ViewModels
                        receiptOfGoodsViewModel,
                        issuingGoodsViewModel,
                        transferOfGoodsViewModel,
                        returnOfGoodsViewModel,
                        writeOffOfGoodsViewModel,
                        orderingGoodsViewModel,
                        virtualWarehouseViewModel,

                        // Settings screens ViewModels
                        productsViewModel,
                        suppliersViewModel,
                        warehousesViewModel,
                        costCentersViewModel,
                        locationsViewModel,
                        inventoryListsViewModel,
                        inventoryGroupsViewModel,
                    )
                }
            }
        }
    }
}