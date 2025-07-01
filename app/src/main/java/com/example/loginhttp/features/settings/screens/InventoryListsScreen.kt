package com.example.loginhttp.features.settings.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.R
import com.example.loginhttp.features.settings.viewmodel.InventoryListsViewModel
import com.example.loginhttp.model.CardAction
import com.example.loginhttp.navigation.AppRoutes
import com.example.loginhttp.navigation.BottomNavBar
import com.example.loginhttp.navigation.UnifiedFloatingActionButton
import com.example.loginhttp.navigation.UnifiedTopAppBar
import com.example.loginhttp.ui.components.ConfirmDeleteDialog
import com.example.loginhttp.ui.components.SearchBar
import com.example.loginhttp.ui.components.SelectionToolbar
import com.example.loginhttp.ui.components.UnifiedItemCard
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun InventoryListsScreen(viewModel: InventoryListsViewModel) {

    val inventoryLists by viewModel.items.collectAsState()

    val selectedItems by viewModel.selectedItems.collectAsState()
    val isInSelectionMode = selectedItems.isNotEmpty()

    val pendingDeleteIds by viewModel.pendingDeleteIds.collectAsState()

    BackHandler(enabled = isInSelectionMode) {
        viewModel.clearSelection()
    }

    Scaffold {
        Column(
            Modifier
                .fillMaxSize()
                .background(LightGray)
        ) {
            if (isInSelectionMode) {
                SelectionToolbar(
                    selectedCount = selectedItems.size,
                    { viewModel.selectAll(inventoryLists.map { it.id }) },
                    actions = buildList {
                        add(Icons.Default.Visibility to { viewModel.changeSelectedItemVisibility() })
                        add(Icons.Default.Delete to { viewModel.confirmDelete(selectedItems.toList()) })
                    }
                )
            }

            SearchBar(
                value = viewModel.searchQuery,
                onValueChange = viewModel::onSearchChange,
                placeholderText = "${stringResource(R.string.search_inventory_lists)} (${inventoryLists.size})"
            )

            LazyColumn(
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(inventoryLists) { inventoryList ->
                    UnifiedItemCard(
                        id = null,
                        icon = if (inventoryList.isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        iconTint = DeepNavy,
                        isSelected = selectedItems.contains(inventoryList.id),
                        selectionMode = isInSelectionMode,
                        onClick = {
                            if (isInSelectionMode) viewModel.toggleSelection(inventoryList.id)
                        },
                        onLongPress = {
                            viewModel.toggleSelection(inventoryList.id)
                        },
                        infoRows = listOf(
                            null to inventoryList.name
                        ),
                        actions = buildList {
                            add(
                                CardAction(stringResource(R.string.delete), Icons.Default.Delete) {
                                    viewModel.confirmDelete(listOf(inventoryList.id))
                                }
                            )
                            add(
                                CardAction(stringResource(R.string.show_hide), if (inventoryList.isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility) {
                                    viewModel.changeItemVisibility(inventoryList)
                                }
                            )
                        }
                    )
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
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewInventoryListsScreen() {
    val mockViewModel: InventoryListsViewModel = viewModel()

    val mockFAB =  @Composable {
        UnifiedFloatingActionButton(
            icon = Icons.Default.Download,
            contentDescription = "Download",
            onClick = { mockViewModel.downloadItems() },
        )
    }

    Scaffold(
        topBar = {
            UnifiedTopAppBar(title = "Inventurne liste")
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = AppRoutes.SETTINGS,
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
            InventoryListsScreen(viewModel = mockViewModel)
        }
    }
}