package com.example.loginhttp.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.loginhttp.features.inventory.InventoryViewModel
import com.example.loginhttp.MainViewModel
import com.example.loginhttp.R
import com.example.loginhttp.features.settings.navigation.SettingsViewModels
import com.example.loginhttp.features.settings.navigation.settingsNavGraph
import com.example.loginhttp.features.warehouse.navigation.WarehouseViewModels
import com.example.loginhttp.features.warehouse.navigation.warehouseNavGraph
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.utils.SetStatusBarColor
import kotlinx.coroutines.launch

@Composable
fun MainAppHost(
    navHostController: NavHostController,
    viewModel: MainViewModel = viewModel(),
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val fabContent = remember { mutableStateOf<@Composable (() -> Unit)?>(null) }

    // Tab-specific nav controllers
    val navControllers = AppRoutes.mainTabs.associateWith { rememberNavController() }
    val selectedTab = rememberSaveable { mutableStateOf(AppRoutes.HOME) }
    val currentRoute = navControllers[selectedTab.value]
        ?.currentBackStackEntryAsState()
        ?.value
        ?.destination
        ?.route

    val title = stringResource(routeTitleMap[currentRoute] ?: R.string.app_name)

    val showBars = shouldShowBars(currentRoute)

    val inventoryViewModel: InventoryViewModel = viewModel()

    val warehouseViewModels = WarehouseViewModels(
        receiptOfGoodsViewModel = viewModel(),
        issuingGoodsViewModel = viewModel(),
        transferOfGoodsViewModel = viewModel(),
        returnOfGoodsViewModel = viewModel(),
        writeOffOfGoodsViewModel = viewModel(),
        orderingGoodsViewModel = viewModel(),
        virtualWarehouseViewModel = viewModel(),
        templatesViewModel = viewModel(),
    )

    val settingsViewModels = SettingsViewModels(
        settingsViewModel = viewModel(),
        productsViewModel = viewModel(),
        suppliersViewModel = viewModel(),
        warehousesViewModel = viewModel(),
        costCentersViewModel = viewModel(),
        locationsViewModel = viewModel(),
        inventoryListsViewModel = viewModel(),
        inventoryGroupsViewModel = viewModel()
    )

    SetStatusBarColor(color = DeepNavy, darkIcons = false)

    NavigationDrawer(
        scope = scope,
        drawerState = drawerState,
        onLogout = {
            viewModel.logout()
            navHostController.navigate(AppRoutes.LOGIN) {
                popUpTo(AppRoutes.MAIN) { inclusive = true }
            }
        },
    ) {
        Scaffold(
            topBar = {
                if (showBars) {
                    UnifiedTopAppBar(
                        title = title,
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        }
                    )
                }
            },
            bottomBar = {
                if (showBars) {
                    BottomNavBar(
                        selectedTab = selectedTab.value,
                        onTabSelected = { selectedTab.value = it },
                    )
                }
            },
            floatingActionButton = fabContent.value ?: {},
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Crossfade(targetState = selectedTab.value) { tab ->
                    NavHost(
                        navController = navControllers[tab]!!,
                        startDestination = tab,
                        enterTransition = {
                            EnterTransition.None
                        },
                        exitTransition = {
                            ExitTransition.None
                        }
                    ) {
                        when (tab) {
                            AppRoutes.HOME, AppRoutes.INVENTORY -> {
                                mainNavGraph(
                                    fabContent = fabContent,
                                    inventoryViewModel = inventoryViewModel
                                )
                            }

                            AppRoutes.WAREHOUSE -> {
                                warehouseNavGraph(
                                    navHostController = navControllers[tab]!!,
                                    fabContent = fabContent,
                                    viewModels = warehouseViewModels
                                )
                            }

                            AppRoutes.SETTINGS -> {
                                settingsNavGraph(
                                    navHostController = navControllers[tab]!!,
                                    fabContent = fabContent,
                                    viewModels = settingsViewModels
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Reusable UI control logic for TopBar/BottomBar
private fun shouldShowBars(route: String?): Boolean {
    return route in AppRoutes.mainTabs
            || route in WarehouseRoutes.all
            || route in SettingsRoutes.all
}