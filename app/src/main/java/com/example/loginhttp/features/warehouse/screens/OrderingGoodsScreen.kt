package com.example.loginhttp.features.warehouse.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.loginhttp.features.warehouse.viewmodel.OrderingGoodsViewModel
import com.example.loginhttp.model.CardAction
import com.example.loginhttp.ui.components.BottomSheetWithModes
import com.example.loginhttp.ui.components.FieldType
import com.example.loginhttp.ui.components.FormField
import com.example.loginhttp.ui.components.FormMode
import com.example.loginhttp.features.warehouse.model.OrderItem
import com.example.loginhttp.features.warehouse.model.OrderStatus
import com.example.loginhttp.features.warehouse.model.OrderType
import com.example.loginhttp.navigation.BottomNavBar
import com.example.loginhttp.navigation.UnifiedFAB
import com.example.loginhttp.ui.components.ConfirmDeleteDialog
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.components.SelectionToolbar
import com.example.loginhttp.ui.components.UnifiedItemCard
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White
import com.example.loginhttp.ui.utils.SetStatusBarColor
import kotlinx.coroutines.launch

@Composable
fun OrderingGoodsScreen(viewModel: OrderingGoodsViewModel) {

    val filteredOrders by viewModel.filteredOrders.collectAsState()
    val isSheetVisible by viewModel.isSheetVisible.collectAsState()

    val selectedItems by viewModel.selectedItems.collectAsState()
    val isInSelectionMode = selectedItems.isNotEmpty()

    val pendingDeleteIds by viewModel.pendingDeleteIds.collectAsState()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val selectedTypeTab by viewModel.selectedTypeTab.collectAsState()
    val selectedSyncTab by viewModel.selectedSyncTab.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = selectedSyncTab.toTabIndex(),
        pageCount = { 2 }
    )
    val scope = rememberCoroutineScope()

    val prevType = remember { mutableIntStateOf(selectedTypeTab.toTabIndex()) }
    val prevSync = remember { mutableIntStateOf(pagerState.currentPage) }

    val syncedCount by viewModel.syncedCount.collectAsState()
    val unsyncedCount by viewModel.unsyncedCount.collectAsState()

    // Reflect pager page into ViewModel
    LaunchedEffect(pagerState.currentPage) {
        viewModel.updateTabs(
            syncTab = tabIndexToOrderStatus(pagerState.currentPage),
            typeTab = selectedTypeTab
        )
    }

    // Refresh filters when order type changes
    LaunchedEffect(selectedTypeTab) {
        pagerState.scrollToPage(0)
        viewModel.updateTabs(
            syncTab = OrderStatus.U_PROCESU,
            typeTab = selectedTypeTab
        )
    }

    // Reset selection when type or sync tab changes
    LaunchedEffect(selectedTypeTab, pagerState.currentPage) {
        if (prevType.intValue != selectedTypeTab.toTabIndex()  || prevSync.intValue != pagerState.currentPage) {
            viewModel.clearSelection()
            prevType.intValue = selectedTypeTab.toTabIndex()
            prevSync.intValue = pagerState.currentPage
        }
    }

    BackHandler(enabled = isInSelectionMode) {
        viewModel.clearSelection()
    }

    SetStatusBarColor(color = DeepNavy, darkIcons = false)

    Scaffold { innerPadding  ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightGray)
        ) {
            MenuHeader(screenWidth = screenWidth, title = "Naručivanje robe")

            if (isInSelectionMode) {
                SelectionToolbar(
                    selectedCount = selectedItems.size,
                    onSelectAll = {
                        viewModel.selectAll(filteredOrders.map { it.id })
                    },
                    actions = buildList {
                        if (selectedSyncTab == OrderStatus.U_PROCESU) {
                            add(Icons.Default.Sync to { viewModel.syncSelectedOrders() })
                            add(Icons.Default.Delete to { viewModel.confirmDelete(selectedItems.toList()) })
                            add(Icons.Default.Warehouse to { /* Prebaci na skladište action */ })
                        }
                    }
                )
            }

            // Type selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepNavy)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        viewModel.updateTabs(
                            syncTab = selectedSyncTab,
                            typeTab = OrderType.INTERNAL
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTypeTab == OrderType.INTERNAL) MassecRed else DeepNavy,
                        contentColor = White
                    )
                ) { Text("Interne narudžbe") }

                Button(
                    onClick = {
                        viewModel.updateTabs(
                            syncTab = selectedSyncTab,
                            typeTab = OrderType.EXTERNAL
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTypeTab == OrderType.EXTERNAL) MassecRed else DeepNavy,
                        contentColor = White
                    )
                ) { Text("Vanjske narudžbe") }
            }

            // Sync status tabs
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = White,
                contentColor = DarkText,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MassecRed
                    )
                },
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(0)
                        }
                        viewModel.updateTabs(
                            syncTab = tabIndexToOrderStatus(0),
                            typeTab = selectedTypeTab
                        )
                    },
                    text = { Text("Nesinkronizirane ($unsyncedCount)") }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(1)
                        }
                        viewModel.updateTabs(
                            syncTab = tabIndexToOrderStatus(1),
                            typeTab = selectedTypeTab
                        )
                    },
                    text = { Text("Sinkronizirane ($syncedCount)") }
                    )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) {page ->
                val visibleOrders = filteredOrders.filter {
                    if (page == 0) {
                        it.status == OrderStatus.U_PROCESU
                    } else {
                        it.status == OrderStatus.ZATVORENO
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(visibleOrders, key = { it.id }) { order ->
                        UnifiedItemCard(
                            id = order.id.toString(),
                            icon = when (order) {
                                is OrderItem.InternalOrder -> Icons.Default.Home
                                is OrderItem.ExternalOrder -> Icons.Default.LocalShipping
                            },
                            iconTint = if (order.status == OrderStatus.U_PROCESU) MassecRed else DeepNavy,
                            isSynced = OrderStatus.ZATVORENO == order.status,
                            isSelected = selectedItems.contains(order.id),
                            selectionMode = isInSelectionMode,
                            onClick = {
                                if (isInSelectionMode) viewModel.toggleSelection(order.id)
                            },
                            onLongPress = {
                                viewModel.toggleSelection(order.id)
                            },
                            infoRows = buildList {
                                add("Dodano" to order.timestamp)
                                when (order) {
                                    is OrderItem.InternalOrder -> {
                                        add("Sa" to order.fromLocation)
                                        add("Na" to order.toLocation)
                                    }

                                    is OrderItem.ExternalOrder -> {
                                        add("Dobavljač" to order.supplier)
                                        add("Skladište" to order.warehouse)
                                    }
                                }
                                add("Status" to if (order.status == OrderStatus.U_PROCESU) "U procesu" else "Zatvoreno")
                            },
                            actions = buildList {
                                if (order.status == OrderStatus.U_PROCESU) {
                                    add(CardAction("Sinkroniziraj", Icons.Default.Sync) {
                                        viewModel.syncOrder(order.id)
                                        }
                                    )
                                    add(CardAction("Izbriši", Icons.Default.Delete) {
                                        viewModel.confirmDelete(listOf(order.id))
                                        }
                                    )
                                    add(CardAction("Prebaci na skladište", Icons.Default.Warehouse) {
                                        // Transfer action
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }

            if (pendingDeleteIds.isNotEmpty()) {
                ConfirmDeleteDialog(
                    itemCount = pendingDeleteIds.size,
                    onConfirm = {
                        viewModel.deleteSelected()
                    },
                    onDismiss = {
                        viewModel.clearPendingDelete()
                    }
                )
            }

            if (isSheetVisible) {
                val formModes = listOf(
                    FormMode(
                        name = "Interna narudžba",
                        fields = listOf(
                            FormField(
                                "Sa lokacije",
                                FieldType.DROPDOWN,
                                listOf("Skladište A", "Skladište B", "Skladište C")
                            ),
                            FormField(
                                "Na lokaciju",
                                FieldType.DROPDOWN,
                                listOf("Lokacija 1", "Lokacija 2", "Lokacija 3")
                            )
                        )
                    ),
                    FormMode(
                        name = "Vanjska narudžba",
                        fields = listOf(
                            FormField(
                                "Dobavljač",
                                FieldType.DROPDOWN,
                                listOf("Dobavljač A", "Dobavljač B", "Dobavljač C")
                            ),
                            FormField(
                                "Skladište",
                                FieldType.DROPDOWN,
                                listOf("Skladište X", "Skladište Y", "Skladište Z")
                            )
                        )
                    )
                )

                BottomSheetWithModes(
                    title = "Dodaj narudžbu",
                    modeSelectorLabel = "Tip narudžbe",
                    modes = formModes,
                    onDismiss = { viewModel.toggleSheet(false) },
                    onSubmit = { mode, values ->
                        if (mode.name == "Interna narudžba") {
                            val fromLocation = values[0]
                            val toLocation = values[1]
                            viewModel.addInternalOrder(fromLocation, toLocation)
                        } else {
                            val supplier = values[0]
                            val warehouse = values[1]
                            viewModel.addExternalOrder(supplier, warehouse)
                        }
                    },
                )
            }
        }
    }
}

fun OrderStatus.toTabIndex() = when (this) {
    OrderStatus.U_PROCESU -> 0
    OrderStatus.ZATVORENO -> 1
}

fun OrderType.toTabIndex() = when (this) {
    OrderType.INTERNAL -> 0
    OrderType.EXTERNAL -> 1
}

fun tabIndexToOrderStatus(index: Int) = when (index) {
    0 -> OrderStatus.U_PROCESU
    else -> OrderStatus.ZATVORENO
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
fun OrderingGoodsScreenPreview() {
    val mockViewModel: OrderingGoodsViewModel = viewModel()

    val mockFAB = @Composable {
        UnifiedFAB(
            icon = Icons.Default.Add,
            contentDescription = "Add",
            onClick = { mockViewModel.toggleSheet(true) }
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = rememberNavController()
            )
        },
        floatingActionButton = mockFAB,
    ) {
        OrderingGoodsScreen(
            viewModel = mockViewModel
        )
    }
}