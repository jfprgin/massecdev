package com.example.loginhttp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.WarehousesViewModel
import com.example.loginhttp.model.CardAction
import com.example.loginhttp.ui.components.BottomNavBar
import com.example.loginhttp.ui.components.ConfirmDeleteDialog
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.components.SearchBar
import com.example.loginhttp.ui.components.SelectionToolbar
import com.example.loginhttp.ui.components.UnifiedItemCard
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.White
import com.example.loginhttp.ui.utils.SetStatusBarColor

@Composable
fun WarehousesScreen(
    selectedScreen: String = "Settings",
    onNavigate: (String) -> Unit,
) {
    val viewModel: WarehousesViewModel = viewModel()

    val warehouses by viewModel.items.collectAsState()

    val selectedItems by viewModel.selectedItems.collectAsState()
    val isInSelectionMode = selectedItems.isNotEmpty()

    val pendingDeleteIds by viewModel.pendingDeleteIds.collectAsState()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    BackHandler(enabled = isInSelectionMode) {
        viewModel.clearSelection()
    }

    SetStatusBarColor(color = DeepNavy, darkIcons = false)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.downloadItems() },
                contentColor = DeepNavy,
                containerColor = DeepNavy,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Download,
                    contentDescription = "Download",
                    tint = White
                )
            }
        },

        bottomBar = {
            BottomNavBar(selectedScreen = selectedScreen, onNavigate = onNavigate)
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightGray)
        ) {
            MenuHeader(screenWidth = screenWidth, title = "Skladišta")

            if (isInSelectionMode) {
                SelectionToolbar(
                    selectedCount = selectedItems.size,
                    onSelectAll = { viewModel.selectAll(warehouses.map { it.id })},
                    actions = listOf(
                        Icons.Default.Delete to { viewModel.confirmDelete(selectedItems.toList()) }
                    )
                )
            }

            SearchBar(
                value = viewModel.searchQuery,
                onValueChange = viewModel::onSearchChange,
                placeholderText = "Pretraži skladišta (${warehouses.size})",
            )

            LazyColumn(
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(warehouses) { warehouse ->
                    UnifiedItemCard(
                        id = warehouse.id.toString(),
                        icon = null,
                        isSelected = selectedItems.contains(warehouse.id),
                        selectionMode = isInSelectionMode,
                        onClick = {
                            if (isInSelectionMode) viewModel.toggleSelection(warehouse.id)
                        },
                        onLongPress = {
                            viewModel.toggleSelection(warehouse.id)
                        },
                        infoRows = listOf(
                            null to warehouse.name
                        ),
                        actions = listOf(
                            CardAction("Izbriši", Icons.Default.Delete) {
                                viewModel.confirmDelete(listOf(warehouse.id))
                            }
                        )
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WarehousesScreenPreview() {
    WarehousesScreen(
        onNavigate = {}
    )
}