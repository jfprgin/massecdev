package com.example.loginhttp.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.loginhttp.model.OrderItem
import com.example.loginhttp.model.OrderType
import com.example.loginhttp.ui.components.BottomNavBar
import com.example.loginhttp.ui.components.ConfirmDeleteDialog
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.components.SelectionToolbar
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

    val pagerState = rememberPagerState(initialPage = viewModel.selectedSyncTab, pageCount = { 2 })
    val scope = rememberCoroutineScope()

    val prevType = remember { mutableIntStateOf(viewModel.selectedTypeTab) }
    val prevSync = remember { mutableIntStateOf(pagerState.currentPage) }

    // Reflect pager page into ViewModel
    LaunchedEffect(pagerState.currentPage) {
        viewModel.updateTabs(syncTab = pagerState.currentPage, typeTab = viewModel.selectedTypeTab)
    }

    // Refresh filters when order type changes
    LaunchedEffect(viewModel.selectedTypeTab) {
        pagerState.scrollToPage(0)
        viewModel.updateTabs(syncTab = 0, typeTab = viewModel.selectedTypeTab)
    }

    // Reset selection when type or sync tab changes
    LaunchedEffect(viewModel.selectedTypeTab, pagerState.currentPage) {
        if (viewModel.selectedTypeTab != prevType.intValue || pagerState.currentPage != prevSync.intValue) {
            viewModel.clearSelection()
            prevType.intValue = viewModel.selectedTypeTab
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
                        if (viewModel.selectedSyncTab == 0) {
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
                    onClick = { viewModel.updateTabs(pagerState.currentPage, 0) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.selectedTypeTab == 0) MassecRed else DeepNavy,
                        contentColor = White
                    )
                ) { Text("Interne narudžbe") }

                Button(
                    onClick = { viewModel.updateTabs(pagerState.currentPage, 1) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.selectedTypeTab == 1) MassecRed else DeepNavy,
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
                        viewModel.updateTabs(syncTab = 0, typeTab = viewModel.selectedTypeTab)
                    },
                    text = { Text("Nesinkronizirane") }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(1)
                        }
                        viewModel.updateTabs(syncTab = 1, typeTab = viewModel.selectedTypeTab)
                    },
                    text = { Text("Sinkronizirane") }
                    )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) {page ->
                val pageOrders = filteredOrders.filter { order ->
                    if (page == 0) !order.synced else order.synced
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(pageOrders, key = { it.id }) { order ->
                        OrderItemCard(
                            order = order,
                            isSelected = selectedItems.contains(order.id),
                            selectionMode = isInSelectionMode,
                            onClick = {
                                if (isInSelectionMode) {
                                    viewModel.toggleSelection(order.id)
                                }
                            },
                            onLongPress = if (!order.synced) {
                                { viewModel.toggleSelection(order.id) }
                            } else {
                                null
                            },
                            onDelete = {
                                viewModel.confirmDelete(listOf(order.id))
                            },
                            onSync = { viewModel.syncOrder(order.id) },
                            onTransfer = { /* TODO: Prebaci na skladište action */ },
                            showSync = !order.synced
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
                AddOrderBottomSheet(
                    onDismiss = { viewModel.toggleSheet(false) },
                    onSubmit = { from, to ->
                        viewModel.addOrder(from, to, if (viewModel.selectedTypeTab == 0) OrderType.INTERNAL else OrderType.EXTERNAL)
                        viewModel.toggleSheet(false)
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
                imageVector = when (order.type) {
                    OrderType.INTERNAL -> Icons.Default.Home
                    OrderType.EXTERNAL -> Icons.Default.LocalShipping
                },
                contentDescription =  when (order.type) {
                    OrderType.INTERNAL -> "Internal"
                    OrderType.EXTERNAL -> "External"
                },
                tint =  if (order.synced) DeepNavy else MassecRed,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Narudžba ${order.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepNavy
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Dodano: ${order.timestamp}", fontSize = 12.sp, color = DarkText)
                Text("Sa lokacije: ${order.fromLocation}", fontSize = 12.sp, color = DarkText)
                Text("Na lokaciju: ${order.toLocation}", fontSize = 12.sp, color = DarkText)
                Text("Status: ${order.status}", fontSize = 12.sp, color = DarkText)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderBottomSheet(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit,
) {
    var fromLocation by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Dodaj novu narudžbu", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fromLocation,
                onValueChange = { fromLocation = it },
                label = { Text("Sa lokacije") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = toLocation,
                onValueChange = { toLocation = it },
                label = { Text("Na lokaciju") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (fromLocation.isNotBlank() && toLocation.isNotBlank()) {
                        onSubmit(fromLocation, toLocation)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text("Dodaj narudžbu")
            }
        }
    }
}

@Preview
@Composable
fun OrderingGoodsScreenPreview() {
    OrderingGoodsScreen(
        onNavigate = {}
    )
}