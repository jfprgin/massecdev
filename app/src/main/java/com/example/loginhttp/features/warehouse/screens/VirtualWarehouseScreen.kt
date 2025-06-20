package com.example.loginhttp.features.warehouse.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.R
import com.example.loginhttp.features.warehouse.model.VirtualWarehouseItem
import com.example.loginhttp.features.warehouse.viewmodel.VirtualWarehouseViewModel
import com.example.loginhttp.model.CardAction
import com.example.loginhttp.navigation.AppRoutes
import com.example.loginhttp.navigation.BottomNavBar
import com.example.loginhttp.navigation.UnifiedFloatingActionButton
import com.example.loginhttp.navigation.UnifiedTopAppBar
import com.example.loginhttp.ui.components.BottomSheet
import com.example.loginhttp.ui.components.ConfirmDeleteDialog
import com.example.loginhttp.ui.components.FieldType
import com.example.loginhttp.ui.components.FormField
import com.example.loginhttp.ui.components.SelectionToolbar
import com.example.loginhttp.ui.components.UnifiedItemCard
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White
import com.example.loginhttp.ui.utils.SetStatusBarColor
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun VirtualWarehouseScreen(viewModel: VirtualWarehouseViewModel) {

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
    val screenWith = LocalConfiguration.current.screenWidthDp.dp

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

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGray)
        ) {
            Column {
//                MenuHeader(screenWidth = screenWith, title = "Virtualno skladište")

                if (isInSelectionMode) {
                    SelectionToolbar(
                        selectedCount = selectedItems.size,
                        onSelectAll = {
                            val relevantItems = if (pagerState.currentPage == 0) {
                                unsyncedItems
                            } else {
                                syncedItems
                            }
                            viewModel.selectAll(relevantItems.map { it.id })
                        },
                        actions = buildList {
                            if (pagerState.currentPage == 0) {
                                add(Icons.Default.Sync to { viewModel.syncSelectedItems() })
                                add(Icons.Default.Delete to { viewModel.confirmDelete(selectedItems.toList()) })
                            }
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

                VirtualWarehouseHorizontalPager(
                    pagerState,
                    unsyncedItems,
                    syncedItems,
                    selectedItems,
                    isInSelectionMode,
                    viewModel
                )
            }

            if (pendingDeletedIds.isNotEmpty()) {
                ConfirmDeleteDialog(
                    itemCount = pendingDeletedIds.size,
                    onConfirm = { viewModel.deleteSelected() },
                    onDismiss = { viewModel.clearPendingDelete() },
                )
            }

            if (isSheetVisible) {
                VirtualWarehouseBottomSheet(viewModel)
            }
        }
    }
}

@Composable
private fun VirtualWarehouseHorizontalPager(
    pagerState: PagerState,
    unsyncedItems: List<VirtualWarehouseItem>,
    syncedItems: List<VirtualWarehouseItem>,
    selectedItems: Set<Int>,
    isInSelectionMode: Boolean,
    viewModel: VirtualWarehouseViewModel
) {
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
                VirtualWarehouseItemCard(item, selectedItems, isInSelectionMode, viewModel, pagerState)
            }
        }
    }
}

@Composable
private fun VirtualWarehouseItemCard(
    item: VirtualWarehouseItem,
    selectedItems: Set<Int>,
    isInSelectionMode: Boolean,
    viewModel: VirtualWarehouseViewModel,
    pagerState: PagerState
) {
    val id = item.id.toString()
    val icon = if (item.synced) Icons.Filled.Lock else Icons.Filled.LockOpen
    val iconTint = if (item.synced) DeepNavy else MassecRed

    val onClick = {
        if (isInSelectionMode) viewModel.toggleSelection(item.id)
    }
    val onLongPress = {
        if (pagerState.currentPage == 0) {
            viewModel.toggleSelection(item.id)
        }
    }

    val infoRows = listOf(
        stringResource(R.string.time) to item.timestamp,
        stringResource(R.string.warehouse) to item.warehouse,
        stringResource(R.string.added) to item.addedBy
    )
    val actions = buildList {
        if (!item.synced) {
            add(
                CardAction(stringResource(R.string.synchronize), Icons.Default.Sync) {
                    viewModel.syncItem(item.id)
                }
            )
            add(
                CardAction(stringResource(R.string.delete), Icons.Default.Delete) {
                    viewModel.confirmDelete(listOf(item.id))
                }
            )
        }
    }

    UnifiedItemCard(
        id = id,
        icon = icon,
        iconTint = iconTint,
        isSynced = item.synced,
        isSelected = selectedItems.contains(item.id),
        selectionMode = isInSelectionMode,
        onClick = onClick,
        onLongPress = onLongPress,
        infoRows = infoRows,
        actions = actions,
    )
}

@Composable
private fun VirtualWarehouseBottomSheet(viewModel: VirtualWarehouseViewModel) {
    val fields = listOf(
        FormField(
            stringResource(R.string.warehouse),
            FieldType.DROPDOWN,
            listOf("Skladište 1", "Skladište 2", "Skladište 3"),
        ),
        // TODO: Korisnik se ne odabire, već se automatski dodaje
        FormField(
            stringResource(R.string.added),
            FieldType.DROPDOWN,
            listOf("Korisnik 1", "Korisnik 2", "Korisnik 3"),
        )
    )

    BottomSheet(
        title = stringResource(R.string.virtual_warehouse_title),
        fields = fields,
        onDismiss = { viewModel.toggleSheet(false) },
        onSubmit = { values ->
            val (warehouse, addedBy) = values
            viewModel.addItem(warehouse, addedBy)
            viewModel.toggleSheet(false)
        },
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewVirtualWarehouseScreen() {
    val mockViewModel: VirtualWarehouseViewModel = viewModel()

    val mockFAB = @Composable {
        UnifiedFloatingActionButton(
            icon = Icons.Default.Add,
            contentDescription = "Add",
            onClick = { mockViewModel.toggleSheet(true) }
        )
    }

    Scaffold(
        topBar = {
            UnifiedTopAppBar(title = "Virtualno skladište")
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
            VirtualWarehouseScreen(
                viewModel = mockViewModel
            )
        }
    }
}