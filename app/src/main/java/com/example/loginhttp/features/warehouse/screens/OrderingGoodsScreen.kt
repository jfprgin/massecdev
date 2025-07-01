package com.example.loginhttp.features.warehouse.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.R
import com.example.loginhttp.features.warehouse.viewmodel.OrderingGoodsViewModel
import com.example.loginhttp.model.CardAction
import com.example.loginhttp.ui.components.BottomSheetWithModes
import com.example.loginhttp.ui.components.FieldType
import com.example.loginhttp.ui.components.FormField
import com.example.loginhttp.ui.components.FormMode
import com.example.loginhttp.features.warehouse.model.OrderItem
import com.example.loginhttp.features.warehouse.model.OrderStatus
import com.example.loginhttp.features.warehouse.model.OrderType
import com.example.loginhttp.navigation.AppRoutes
import com.example.loginhttp.navigation.BottomNavBar
import com.example.loginhttp.navigation.UnifiedFloatingActionButton
import com.example.loginhttp.navigation.UnifiedTopAppBar
import com.example.loginhttp.ui.components.ConfirmDeleteDialog
import com.example.loginhttp.ui.components.SelectionToolbar
import com.example.loginhttp.ui.components.UnifiedItemCard
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OrderingGoodsScreen(viewModel: OrderingGoodsViewModel) {

    val filteredOrders by viewModel.filteredOrders.collectAsState()
    val isSheetVisible by viewModel.isSheetVisible.collectAsState()

    val selectedItems by viewModel.selectedItems.collectAsState()
    val isInSelectionMode = selectedItems.isNotEmpty()

    val pendingDeleteIds by viewModel.pendingDeleteIds.collectAsState()

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

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGray)
        ) {
            if (isInSelectionMode) {
                OrderingGoodsSelectionToolbar(selectedItems, viewModel, filteredOrders, selectedSyncTab)
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

            OrderingGoodsHorizontalPager(pagerState, filteredOrders, selectedItems, isInSelectionMode, viewModel)

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
                OrderingGoodsBottomSheet(viewModel)
            }
        }
    }
}

@Composable
private fun OrderingGoodsSelectionToolbar(
    selectedItems: Set<Int>,
    viewModel: OrderingGoodsViewModel,
    filteredOrders: List<OrderItem>,
    selectedSyncTab: OrderStatus
) {
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

@Composable
private fun OrderingGoodsHorizontalPager(
    pagerState: PagerState,
    filteredOrders: List<OrderItem>,
    selectedItems: Set<Int>,
    isInSelectionMode: Boolean,
    viewModel: OrderingGoodsViewModel
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
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
                OrderingGoodsItemCard(order, selectedItems, isInSelectionMode, viewModel)
            }
        }
    }
}

@Composable
private fun OrderingGoodsItemCard(
    order: OrderItem,
    selectedItems: Set<Int>,
    isInSelectionMode: Boolean,
    viewModel: OrderingGoodsViewModel
) {
    val id = order.id.toString()
    val icon = when (order) {
        is OrderItem.InternalOrder -> Icons.Default.Home
        is OrderItem.ExternalOrder -> Icons.Default.LocalShipping
    }
    val iconTint = if (order.status == OrderStatus.U_PROCESU) MassecRed else DeepNavy
    val isSynced = order.status == OrderStatus.ZATVORENO

    val onClick = {
        if (isInSelectionMode) viewModel.toggleSelection(order.id)
    }
    val onLongPress = {
        viewModel.toggleSelection(order.id)
    }

    val infoRows = buildList {
        add(stringResource(R.string.added) to order.timestamp)
        when (order) {
            is OrderItem.InternalOrder -> {
                add(stringResource(R.string.from) to order.fromLocation)
                add(stringResource(R.string.to) to order.toLocation)
            }
            is OrderItem.ExternalOrder -> {
                add(stringResource(R.string.supplier) to order.supplier)
                add(stringResource(R.string.warehouse) to order.warehouse)
            }
        }
        add(stringResource(R.string.status) to if (order.status == OrderStatus.U_PROCESU) stringResource(R.string.processing) else stringResource(R.string.closed))
    }

    val actions = buildList {
        if (order.status == OrderStatus.U_PROCESU) {
            add(CardAction(stringResource(R.string.synchronize), Icons.Default.Sync) {
                viewModel.syncOrder(order.id)
            })
            add(CardAction(stringResource(R.string.delete), Icons.Default.Delete) {
                viewModel.confirmDelete(listOf(order.id))
            })
            add(CardAction(stringResource(R.string.transfer_to_warehouse), Icons.Default.Warehouse) {
                // Transfer action
            })
        }
    }

    UnifiedItemCard(
        id = id,
        icon = icon,
        iconTint = iconTint,
        isSynced = isSynced,
        isSelected = selectedItems.contains(order.id),
        selectionMode = isInSelectionMode,
        onClick = onClick,
        onLongPress = onLongPress,
        infoRows = infoRows,
        actions = actions,
    )
}

@Composable
private fun OrderingGoodsBottomSheet(viewModel: OrderingGoodsViewModel) {
    val formModes = listOf(
        FormMode(
            name = stringResource(R.string.internal_order),
            fields = listOf(
                FormField(
                    stringResource(R.string.from_location),
                    FieldType.DROPDOWN,
                    listOf("Skladište A", "Skladište B", "Skladište C")
                ),
                FormField(
                    stringResource(R.string.to_location),
                    FieldType.DROPDOWN,
                    listOf("Lokacija 1", "Lokacija 2", "Lokacija 3")
                )
            )
        ),
        FormMode(
            name = stringResource(R.string.external_order),
            fields = listOf(
                FormField(
                    stringResource(R.string.supplier),
                    FieldType.DROPDOWN,
                    listOf("Dobavljač A", "Dobavljač B", "Dobavljač C")
                ),
                FormField(
                    stringResource(R.string.warehouse),
                    FieldType.DROPDOWN,
                    listOf("Skladište X", "Skladište Y", "Skladište Z")
                )
            )
        )
    )

    val internalOrder = stringResource(R.string.internal_order)
    BottomSheetWithModes(
        title = stringResource(R.string.add_order),
        modeSelectorLabel = stringResource(R.string.order_type),
        modes = formModes,
        onDismiss = { viewModel.toggleSheet(false) },
        onSubmit = { mode, values ->
            if (mode.name == internalOrder) {
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
        UnifiedFloatingActionButton(
            icon = Icons.Default.Add,
            contentDescription = "Add",
            onClick = { mockViewModel.toggleSheet(true) }
        )
    }

    Scaffold(
        topBar = {
            UnifiedTopAppBar(title = "Naručivanje robe")
        },

        bottomBar = {
            BottomNavBar(
                selectedTab = AppRoutes.WAREHOUSE,
                onTabSelected = {}
            )
        },
        floatingActionButton = mockFAB,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OrderingGoodsScreen(
                viewModel = mockViewModel
            )
        }
    }
}