package com.example.loginhttp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.TransferOfGoodsViewModel
import com.example.loginhttp.model.TransferOfGoodsItem
import com.example.loginhttp.ui.components.BottomNavBar
import com.example.loginhttp.ui.components.BottomSheet
import com.example.loginhttp.ui.components.ConfirmDeleteDialog
import com.example.loginhttp.ui.components.FieldType
import com.example.loginhttp.ui.components.FormField
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
fun TransferOfGoodsScreen(
    selectedScreen: String = "Warehouse",
    onNavigate: (String) -> Unit = {},
) {
    val viewModel: TransferOfGoodsViewModel = viewModel()

    val items by viewModel.items.collectAsState()
    val isSheetVisible by viewModel.isSheetVisible.collectAsState()

    val selectedItems by viewModel.selectedItems.collectAsState()
    val isInSelectionMode = selectedItems.isNotEmpty()

    val pendingDeletedIds by viewModel.pendingDeleteIds.collectAsState()

    val syncedItems = items.filter { it.synced }
    val unsyncedItems = items.filter { !it.synced }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val scope = rememberCoroutineScope()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val previousPage = remember { mutableIntStateOf(pagerState.currentPage) }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != previousPage.intValue) {
            viewModel.clearSelection()
            previousPage.intValue = pagerState.currentPage
        }
    }

    BackHandler(enabled = isInSelectionMode) {
        viewModel.clearSelection()
    }

    SetStatusBarColor(color = DeepNavy, darkIcons = false)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.toggleSheet(true)},
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightGray)
        ) {
            Column {
                MenuHeader(screenWidth = screenWidth, title = "Prijenos robe")

                if (isInSelectionMode) {
                    SelectionToolbar(
                        selectedCount = selectedItems.size,
                        onSelectAll = {
                            val relevantItems = if (pagerState.currentPage == 0) {
                                syncedItems
                            } else {
                                unsyncedItems
                            }
                            viewModel.selectAll(relevantItems.map { it.id })
                        },
                        actions = buildList {
                            if (pagerState.currentPage == 0) {
                                add(Icons.Default.Sync to { viewModel.syncSelectedItems() })
                            }
                            add(Icons.Default.Delete to { viewModel.confirmDelete(selectedItems.toList()) })
                        }
                    )
                }

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
                        },
                        text = { Text("Nesinkronizirano (${unsyncedItems.size})") }
                    )

                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = {
                            scope.launch {
                                pagerState.scrollToPage(1)
                            }
                        },
                        text = { Text("Sinkronizirano (${syncedItems.size})") }
                    )
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val list = if (page == 0) unsyncedItems else syncedItems
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(list) { item ->
                            TransferOfGoodsItemCard(
                                item = item,
                                isSelected = selectedItems.contains(item.id),
                                selectionMode = isInSelectionMode,
                                onClick = {
                                    if (isInSelectionMode) viewModel.toggleSelection(item.id)
                                },
                                onLongPress = {
                                    viewModel.toggleSelection(item.id)
                                },
                                onDelete = {
                                    viewModel.confirmDelete(listOf(item.id))
                                },
                                onSync = {
                                    if (!item.synced) viewModel.syncItem(item.id)
                                },
                                showSync = !item.synced
                            )
                        }
                    }
                }
            }

            if (pendingDeletedIds.isNotEmpty()) {
                ConfirmDeleteDialog(
                    itemCount = pendingDeletedIds.size,
                    onConfirm = { viewModel.deleteSelected() },
                    onDismiss = { viewModel.clearPendingDelete() },
                )
            }

            if (isSheetVisible) {
                val fields = listOf(
                    FormField(
                        "Skladište",
                        FieldType.DROPDOWN,
                        listOf("Skladište 1", "Skladište 2", "Skladište 3"),
                    ),
                    FormField(
                        "Mjesto troška",
                        FieldType.DROPDOWN,
                        listOf("Mjesto troška 1", "Mjesto troška 2", "Mjesto troška 3"),
                    )
                )

                BottomSheet(
                    title = "Prijenos robe",
                    fields = fields,
                    onDismiss = { viewModel.toggleSheet(false) },
                    onSubmit = { values ->
                        val (warehouse, costCenter) = values
                        viewModel.addItem(warehouse, costCenter)
                        viewModel.toggleSheet(false)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransferOfGoodsItemCard(
    item: TransferOfGoodsItem,
    isSelected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onDelete: () -> Unit,
    onSync: (Int) -> Unit,
    showSync: Boolean
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
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp),
        ) {
            Icon(
                if (item.synced) {
                    Icons.Filled.Lock
                } else {
                    Icons.Filled.LockOpen
                },
                contentDescription = if (item.synced) "Synced" else "Unsynced",
                tint = if (item.synced) DeepNavy else MassecRed,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("Prijenos robe: ${item.id}", fontSize = 18.sp, color = DeepNavy)
                Text(item.timestamp, fontSize = 14.sp, color = DarkText)
            }

            Box {
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
                }else {
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
                    if (showSync) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Sinkroniziraj",
                                    color = DarkText
                                )
                            },
                            onClick = {
                                onSync(item.id)
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
                    }
                    DropdownMenuItem(
                        text = { Text(
                            "Izbriši",
                            color = DarkText
                        ) },
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
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransferOfGoodsScreenPreview() {
    TransferOfGoodsScreen(
        onNavigate = {},
    )
}