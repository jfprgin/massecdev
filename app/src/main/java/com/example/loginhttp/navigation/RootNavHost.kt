package com.example.loginhttp.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.loginhttp.InventoryViewModel
import com.example.loginhttp.IssuingGoodsViewModel
import com.example.loginhttp.OrderingGoodsViewModel
import com.example.loginhttp.R
import com.example.loginhttp.ReceiptOfGoodsViewModel
import com.example.loginhttp.ReturnOfGoodsViewModel
import com.example.loginhttp.TransferOfGoodsViewModel
import com.example.loginhttp.VirtualWarehouseViewModel
import com.example.loginhttp.WriteOffOfGoodsViewModel

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
        val receiptOfGoodsViewModel: ReceiptOfGoodsViewModel = viewModel()
        val issuingGoodsViewModel: IssuingGoodsViewModel = viewModel()
        val transferOfGoodsViewModel: TransferOfGoodsViewModel = viewModel()
        val returnOfGoodsViewModel: ReturnOfGoodsViewModel = viewModel()
        val writeOffOfGoodsViewModel: WriteOffOfGoodsViewModel = viewModel()
        val orderingGoodsViewModel: OrderingGoodsViewModel = viewModel()
        val virtualWarehouseViewModel: VirtualWarehouseViewModel = viewModel()

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

                        // Warehouse screens
                        inventoryViewModel,
                        receiptOfGoodsViewModel,
                        issuingGoodsViewModel,
                        transferOfGoodsViewModel,
                        returnOfGoodsViewModel,
                        writeOffOfGoodsViewModel,
                        orderingGoodsViewModel,
                        virtualWarehouseViewModel,
                    )
                }
            }
        }
    }
}