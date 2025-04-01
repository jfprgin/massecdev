package com.example.loginhttp.ui.screens
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.items
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.lifecycle.viewmodel.compose.viewModel

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
import com.example.loginhttp.InventoryViewModel
import com.example.loginhttp.model.InventoryItem
import com.example.loginhttp.ui.components.BottomNavBar
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.theme.DarkGray
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White
import com.example.loginhttp.ui.components.ConfirmDeleteDialog
import com.example.loginhttp.ui.components.SelectionToolbar
import com.example.loginhttp.ui.utils.SetStatusBarColor
import kotlinx.coroutines.launch

@Composable
fun InventoryScreen(
    selectedScreen: String = "Inventory",
    onNavigate: (String) -> Unit,
) {
    val viewModel: InventoryViewModel = viewModel()

    val items by viewModel.items.collectAsState()
    val isSheetVisible by viewModel.isSheetVisible.collectAsState()

    val selectedItems by viewModel.selectedItems.collectAsState()
    val isInSelectionMode = selectedItems.isNotEmpty()

    val pendingDeleteIds by viewModel.pendingDeleteIds.collectAsState()

    val syncedItems = items.filter { it.synced }
    val unsyncedItems = items.filter { !it.synced }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val scope = rememberCoroutineScope()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Clear selection when switching tabs
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
                MenuHeader(screenWidth = screenWidth, title = "Inventura")

                if (isInSelectionMode) {
                    SelectionToolbar(
                        selectedCount = selectedItems.size,
                        onSelectAll = {
                            val relevantItems = if (pagerState.currentPage == 0) unsyncedItems else syncedItems
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

                // Tab selection
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

                // Paged content
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
                            InventoryItemCard(
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
                                onShowTable = { /* TODO */ },
                                showSync = !item.synced
                            )
                        }
                    }
                }
            }

            if(pendingDeleteIds.isNotEmpty()) {
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
                AddInventoryBottomSheet(
                    onDismiss = { viewModel.toggleSheet(false) },
                    onAddByName = {
                        viewModel.addItem(it)
                        viewModel.toggleSheet(false)
                    },
                    onAddByGroup = {
                        viewModel.addItem(it)
                        viewModel.toggleSheet(false)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInventoryBottomSheet(
    onDismiss: () -> Unit,
    onAddByName: (String) -> Unit,
    onAddByGroup: (String) -> Unit
) {
    var addingByName by remember { mutableStateOf(false) }
    var addingByGroup by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Inventura",
                fontSize = 20.sp,
                color = DeepNavy,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Options (if not yet selected)
            if (!addingByName && !addingByGroup) {
                Button(
                    onClick = { addingByGroup = true },
                    colors = ButtonColors(
                        containerColor = DeepNavy,
                        contentColor = White,
                        disabledContainerColor = DarkGray,
                        disabledContentColor = White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Po grupi",
                        color = White,
                        fontSize = 16.sp
                        )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { addingByName = true },
                    colors = ButtonColors(
                        containerColor = DeepNavy,
                        contentColor = White,
                        disabledContainerColor = DarkGray,
                        disabledContentColor = White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Po nazivu",
                        color = White,
                        fontSize = 16.sp
                        )
                }
            }

            // Add by Name Flow
            if (addingByName) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Naziv") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = DeepNavy,
                        unfocusedIndicatorColor = DarkGray
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        onAddByName(input)
                        input = ""
                        addingByName = false
                        onDismiss()
                    },
                    enabled = input.isNotBlank(),
                    colors = ButtonColors(
                        containerColor = DeepNavy,
                        contentColor = White,
                        disabledContainerColor = DarkGray,
                        disabledContentColor = White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        "U redu",
                        color = White,
                        fontSize = 16.sp
                        )
                }
            }

            // Add by Group Flow
            if (addingByGroup) {
                // Replace with real group list!
                val groups = listOf("Grupa A", "Grupa B", "Grupa C")
                Column {
                    groups.forEach { group ->
                        Button(
                            onClick = {
                                onAddByGroup(group)
                                addingByGroup = false
                                onDismiss()
                            },
                            colors = ButtonColors(
                                containerColor = DeepNavy,
                                contentColor = White,
                                disabledContainerColor = DarkGray,
                                disabledContentColor = White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                group,
                                color = White,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InventoryItemCard(
    item: InventoryItem,
    isSelected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onDelete: () -> Unit,
    onSync: (Int) -> Unit,
    onShowTable: (Int) -> Unit,
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
                Text(item.name, fontSize = 18.sp, color = DeepNavy)
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
                    DropdownMenuItem(
                        text = { Text(
                            "Prikaži tablicu",
                            color = DarkText
                        ) },
                        onClick = {
                            onShowTable(item.id)
                            menuExpanded = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.TableRows,
                                contentDescription = "Show Table",
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
fun InventoryScreenPreview() {
    InventoryScreen(
        onNavigate = {},
    )
}