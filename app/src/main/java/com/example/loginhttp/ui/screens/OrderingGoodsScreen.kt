package com.example.loginhttp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material3.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.OrderingGoodsViewModel
import com.example.loginhttp.model.BottomSheet
import com.example.loginhttp.model.FieldType
import com.example.loginhttp.model.FormField
import com.example.loginhttp.model.OrderItem
import com.example.loginhttp.model.OrderStatus
import com.example.loginhttp.model.OrderType
import com.example.loginhttp.ui.components.BottomNavBar
import com.example.loginhttp.ui.components.ConfirmDeleteDialog
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.components.SelectionToolbar
import com.example.loginhttp.ui.theme.DarkGray
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White
import com.example.loginhttp.ui.utils.SetStatusBarColor
import kotlinx.coroutines.launch

@Composable
fun OrderingGoodsScreen(
    selectedScreen: String = "Warehouse",
    onNavigate: (String) -> Unit,
) {
    val viewModel: OrderingGoodsViewModel = viewModel()

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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.toggleSheet(true) },
                contentColor = DeepNavy,
                containerColor = DeepNavy,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = White
                )
            }
        },

        bottomBar = {
            BottomNavBar(selectedScreen = selectedScreen, onNavigate = onNavigate)
        }
    ) { innerPadding  ->
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
                    text = { Text("Nesinkronizirane") }
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
                    text = { Text("Sinkronizirane") }
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
                        OrderItemCard(
                            order = order,
                            isSelected = selectedItems.contains(order.id),
                            selectionMode = isInSelectionMode,
                            onClick = {
                                if (isInSelectionMode) {
                                    viewModel.toggleSelection(order.id)
                                }
                            },
                            onLongPress = if (order.status == OrderStatus.U_PROCESU) {
                                { viewModel.toggleSelection(order.id) }
                            } else {
                                null
                            },
                            onDelete = {
                                viewModel.confirmDelete(listOf(order.id))
                            },
                            onSync = { viewModel.syncOrder(order.id) },
                            onTransfer = { /* TODO: Prebaci na skladište action */ },
                            showSync = order.status == OrderStatus.U_PROCESU
                        )
                    }
                }
            }

            if (pendingDeleteIds.isNotEmpty()) {
                ConfirmDeleteDialog(
                    itemCount = pendingDeleteIds.size,
                    onConfirm = {
                        viewModel.executeDelete()
                    },
                    onDismiss = {
                        viewModel.clearPendingDelete()
                    }
                )
            }

            if (isSheetVisible) {
                val fields = if (selectedTypeTab == OrderType.INTERNAL) {
                    listOf(
                        FormField("Sa lokacije", FieldType.DROPDOWN, listOf("Skladište A", "Skladište B", "Skladište C")),
                        FormField("Na lokaciju", FieldType.DROPDOWN, listOf("Lokacija 1", "Lokacija 2", "Lokacija 3")),
                    )
                } else {
                    listOf(
                        FormField("Dobavljač", FieldType.DROPDOWN, listOf("Dobavljač A", "Dobavljač B", "Dobavljač C")),
                        FormField("Skladište", FieldType.DROPDOWN, listOf("Skladište X", "Skladište Y", "Skladište Z")),
                    )
                }

                BottomSheet(
                    title = if (selectedTypeTab == OrderType.INTERNAL) {
                        "Dodaj internu narudžbu"
                    } else {
                        "Dodaj vanjsku narudžbu"
                    },
                    fields = fields,
                    onDismiss = { viewModel.toggleSheet(false) },
                    onSubmit = { values ->
                        if (selectedTypeTab == OrderType.INTERNAL) {
                            viewModel.addInternalOrder(
                                fromLocation = values[0],
                                toLocation = values[1]
                            )
                        } else {
                            viewModel.addExternalOrder(
                                supplier = values[0],
                                warehouse = values[1]
                            )
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderItemCard(
    order: OrderItem,
    isSelected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    onDelete: () -> Unit,
    onSync: (Int) -> Unit,
    onTransfer: () -> Unit,
    showSync: Boolean,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) LightGray else White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = when (order) {
                    is OrderItem.InternalOrder -> Icons.Default.Home
                    is OrderItem.ExternalOrder -> Icons.Default.LocalShipping
                },
                contentDescription =  when (order) {
                    is OrderItem.InternalOrder -> "Internal"
                    is OrderItem.ExternalOrder -> "External"
                },
                tint =  if (order.status == OrderStatus.U_PROCESU) MassecRed else DeepNavy,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (order) {
                        is OrderItem.InternalOrder -> "Interna narudžba: ${order.id}"
                        is OrderItem.ExternalOrder -> "Vanjska narudžba: ${order.id}"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepNavy
                )
                Text("Dodano: ${order.timestamp}", fontSize = 12.sp, color = DarkText)

                when (order) {
                    is OrderItem.InternalOrder -> {
                        Text("Sa: ${order.fromLocation}", fontSize = 12.sp, color = DarkText)
                        Text("Na: ${order.toLocation}", fontSize = 12.sp, color = DarkText)
                    }

                    is OrderItem.ExternalOrder -> {
                        Text("Dobavljač: ${order.supplier}", fontSize = 12.sp, color = DarkText)
                        Text("Skladište: ${order.warehouse}", fontSize = 12.sp, color = DarkText)
                    }
                }

                Text(
                    "Status: ${if (order.status.name == "U_PROCESU") "U procesu" else "Zatvoreno"}",
                    fontSize = 12.sp,
                    color = DarkText
                )
            }

            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectionMode) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                if (isSelected) DeepNavy else Color.Transparent,
                                shape = CircleShape
                            )
                            .border(
                                width = 2.dp,
                                color = if (isSelected) DeepNavy else LightGray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = LightGray,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else if (showSync) {
                    IconButton(onClick = { menuExpanded = true}) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = DeepNavy
                        )
                    }
                }

                DropdownMenu(
                    containerColor = White,
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Sinkroniziraj",
                                color = DarkText
                            )
                        },
                        onClick = {
                            onSync(order.id)
                            menuExpanded = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Sync,
                                contentDescription = "Sync",
                                tint = DeepNavy
                            )
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                "Izbriši",
                                color = DarkText
                            )
                        },
                        onClick = {
                            onDelete()
                            menuExpanded = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = DeepNavy
                            )
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                "Prebaci na skladište",
                                color = DarkText
                            )
                        },
                        onClick = {
                            onTransfer()
                            menuExpanded = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Warehouse,
                                contentDescription = "Transfer",
                                tint = DeepNavy
                            )
                        }
                    )
                }
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

@Preview
@Composable
fun OrderingGoodsScreenPreview() {
    OrderingGoodsScreen(
        onNavigate = {}
    )
}