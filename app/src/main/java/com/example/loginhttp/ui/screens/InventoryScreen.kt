package com.example.loginhttp.ui.screens
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.loginhttp.InventoryViewModel
import com.example.loginhttp.model.CardAction
import com.example.loginhttp.navigation.BottomNavBar
import com.example.loginhttp.navigation.UnifiedFAB
import com.example.loginhttp.ui.components.BottomSheetWithModes
import com.example.loginhttp.ui.components.FieldType
import com.example.loginhttp.ui.components.FormField
import com.example.loginhttp.ui.components.FormMode
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White
import com.example.loginhttp.ui.components.ConfirmDeleteDialog
import com.example.loginhttp.ui.components.SelectionToolbar
import com.example.loginhttp.ui.components.UnifiedItemCard
import com.example.loginhttp.ui.utils.SetStatusBarColor
import kotlinx.coroutines.launch

@Composable
fun InventoryScreen(viewModel: InventoryViewModel) {
//    val viewModel: InventoryViewModel = viewModel()

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

    Scaffold { innerPadding ->
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
                            UnifiedItemCard(
                                id = item.id.toString(),
                                icon = if (item.synced) Icons.Filled.Lock else Icons.Filled.LockOpen,
                                iconTint = if (item.synced) DeepNavy else MassecRed,
                                isSynced = item.synced,
                                isSelected = selectedItems.contains(item.id),
                                selectionMode = isInSelectionMode,
                                onClick = {
                                    if (isInSelectionMode) viewModel.toggleSelection(item.id)
                                },
                                onLongPress = {
                                    viewModel.toggleSelection(item.id)
                                },
                                infoRows = buildList {
                                    add("Naziv" to item.name)
                                    add("Vrijeme" to item.timestamp)
                                },
                                actions = buildList {
                                    if (!item.synced) {
                                        add(
                                            CardAction("Sinkroniziraj", Icons.Default.Sync) {
                                            viewModel.syncItem(item.id)
                                            }
                                        )
                                    }
                                    add(CardAction("Izbriši", Icons.Default.Delete) {
                                        viewModel.confirmDelete(listOf(item.id))
                                        }
                                    )
                                    add(
                                        CardAction("Prikaži tablicu", Icons.Default.TableRows) {
                                         // TODO: Implement show table action
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            if(pendingDeleteIds.isNotEmpty()) {
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
                        name = "Po grupi",
                        fields = listOf(
                            FormField(
                                "Grupa",
                                type = FieldType.DROPDOWN,
                                listOf("Grupa A", "Grupa B", "Grupa C")
                            ),
                        )
                    ),
                    FormMode(
                        name = "Po nazivu",
                        fields = listOf(
                            FormField(
                                "Naziv",
                                type = FieldType.TEXT
                            ),
                        )
                    )
                )

                BottomSheetWithModes(
                    title = "Inventura",
                    modeSelectorLabel = "Način unosa",
                    modes = formModes,
                    onDismiss = { viewModel.toggleSheet(false) },
                    onSubmit = { _, values ->
                        val name = values[0]
                        viewModel.addItem(name)
                    }
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InventoryScreenPreview() {
    val mockViewModel: InventoryViewModel = viewModel()

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
                navController = rememberNavController() // can be fake
            )
        },
        floatingActionButton = mockFAB
    ) {
        InventoryScreen(
            viewModel = mockViewModel
        )
    }

}