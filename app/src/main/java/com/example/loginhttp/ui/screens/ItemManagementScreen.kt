package com.example.loginhttp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.ItemManagementViewModel
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
fun ItemManagementScreen(
    selectedScreen: String,
    onNavigate: (String) -> Unit,
) {
    val viewModel: ItemManagementViewModel = viewModel()

    val items by viewModel.items.collectAsState()
    val isSheetVisible by viewModel.isSheetVisible.collectAsState()

    val selectedItems by viewModel.selectedItems.collectAsState()
    val isInSelectionMode = selectedItems.isNotEmpty()

    val pendingDeleteIds by viewModel.pendingDeleteIds.collectAsState()

    var menuExpanded by remember {  mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWith = LocalConfiguration.current.screenWidthDp.dp

    BackHandler(enabled = isInSelectionMode) {
        viewModel.clearSelection()
    }

    SetStatusBarColor(color = DeepNavy, darkIcons = false)

    Scaffold(
        // TODO: Add other actions to the menu and a dropdown menu
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.opeAddItemDialog() },
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
                MenuHeader(screenWidth = screenWith, title = "Narudžba")

                if (isInSelectionMode) {
                    SelectionToolbar(
                        selectedCount = selectedItems.size,
                        onSelectAll = {
                            viewModel.selectAll(items.map { it.id })
                        },
                        actions = listOf(
                           Icons.Default.Delete to { viewModel.confirmDelete(selectedItems.toList()) }
                        )
                    )
                }

                SearchBar(
                    value = viewModel.inlineSearchQuery,
                    onValueChange = viewModel::onInlineSearchChange,
                    placeholderText = "Pretraži artikle (${items.size})",
                )

                LazyColumn(
                    contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items) { item ->
                        UnifiedItemCard(
                            id = item.id.toString(),
                            icon = null,
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
                                add("Barkod" to item.code.toString())
                                add("Mjerna jedinica" to item.unitOfMeasure.toString())
                            },
                            actions = listOf(
                                CardAction("Izbriši", Icons.Default.Delete) {
                                    viewModel.confirmDelete(listOf(item.id))
                                }
                            ),
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
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewItemManagementScreen() {
    ItemManagementScreen(
        selectedScreen = "Settings",
        onNavigate = {}
    )
}