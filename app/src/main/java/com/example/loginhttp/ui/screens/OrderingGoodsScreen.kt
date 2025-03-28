package com.example.loginhttp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.OrderingGoodsViewModel
import com.example.loginhttp.model.OrderItem
import com.example.loginhttp.ui.components.BottomNavBar
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.components.SelectionToolbar
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.White
import com.example.loginhttp.ui.utils.SetStatusBarColor

@Composable
fun OrderingGoodsScreen(
    selectedScreen: String = "Warehouse",
    onNavigate: (String) -> Unit,
) {
    val viewModel: OrderingGoodsViewModel = viewModel()

    val items by viewModel.orderItems.collectAsState()
    val isSheetVisible by viewModel.isSheetVisible.collectAsState()

    val selectedItems by viewModel.selectedItems.collectAsState()
    val isInSelectionMode = selectedItems.isNotEmpty()

    val pendingDeleteIds by viewModel.pendingDeleteIds.collectAsState()

    val externalOrders = items.filter { it.orderType == "Vanjska" }
    val internalOrders = items.filter { it.orderType == "Interna" }

    val syncedOrders = items.filter { it.synced }
    val unsyncedOrders = items.filterNot { it.synced }

    var selectedTypeTab by remember { mutableIntStateOf(0) }   // 0 = Interne, 1 = Vanjske
    var selectedSyncTab by remember { mutableIntStateOf(0) }    // 0 = Nesinkronizirane, 1 = Sinkronizirane

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    /* TODO: Backhandler and clear selection when switching tabs */

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
                        val relevantItem = if (selectedSyncTab == 0) {
                            unsyncedOrders
                        } else {
                            syncedOrders
                        }
                        viewModel.selectAll(relevantItem.map { it.id })
                    },
                    actions = buildList {
                        if (selectedSyncTab == 0) {
                            add(Icons.Default.Sync to { viewModel.syncSelectedOrders() })
                            add(Icons.Default.Delete to { viewModel.confirmDelete(selectedItems.toList()) })
                            add(Icons.Default.TableRows to { /* Prebaci na skladište action */ })
                        }
                    }
                )
            }

            TabRow(selectedTabIndex = selectedTypeTab) {
                Tab(
                    selected = selectedTypeTab == 0,
                    onClick = { selectedTypeTab = 0 }
                ) {
                    Text(
                        "Interne narudžbe (${unsyncedOrders.size})",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Tab(
                    selected = selectedTypeTab == 1,
                    onClick = { selectedTypeTab = 1 }
                ) {
                    Text(
                        "Vanjske narudžbe (${externalOrders.size})",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            TabRow(selectedTabIndex = selectedTypeTab) {
                Tab(
                    selected = selectedSyncTab == 0,
                    onClick = { selectedSyncTab = 0 }
                ) {
                    Text(
                        "Nesinkronizirane (${unsynced.size})",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Tab(
                    selected = selectedSyncTab == 1,
                    onClick = { selectedSyncTab = 1 }
                ) {
                    Text(
                        "Sinkronizirane (${synced.size})",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            val list = if (selectedSyncTab == 0) unsynced else synced

            LazyColumn {
                items(list) { order ->
                    OrderItemCard(
                        order = order,
                        isSynced = order.synced,
                        onClick = { onOrderClick(order) },
                        onLongClick = if (!order.synced) {
                            { onOrderLongClick(order) }
                        } else {
                            null
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderItemCard(
    order: OrderItem,
    isSynced: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = DarkText)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Narudžba ${order.id}", fontWeight = FontWeight.Bold, color = DarkText)
                Text("Dodano: ${order.timestamp}", fontSize = 12.sp, color = DarkText)
                Text("Sa: ${order.fromLocation}", fontSize = 12.sp, color = DarkText)
                Text("Na: ${order.toLocation}", fontSize = 12.sp, color = DarkText)
                Text("Status: ${order.status}", fontSize = 12.sp, color = DarkText)
            }
        }
    }
}

@Preview
@Composable
fun OrderingGoodsScreenPreview() {
    OrderingGoodsScreen(
        internalOrders = listOf(
            OrderItem(1, "2021-09-01", "Lokacija 1", "Lokacija 2", "Narudžba", false),
            OrderItem(2, "2021-09-02", "Lokacija 2", "Lokacija 3", "Narudžba", true),
            OrderItem(3, "2021-09-03", "Lokacija 3", "Lokacija 4", "Narudžba", false),
        ),
        externalOrders = listOf(
            OrderItem(4, "2021-09-04", "Lokacija 4", "Lokacija 5", "Narudžba", true),
            OrderItem(5, "2021-09-05", "Lokacija 5", "Lokacija 6", "Narudžba", false),
            OrderItem(6, "2021-09-06", "Lokacija 6", "Lokacija 7", "Narudžba", true),
        ),
        onAddClick = {},
        onOrderClick = {},
        onOrderLongClick = {},
        onNavigate = {}
    )
}