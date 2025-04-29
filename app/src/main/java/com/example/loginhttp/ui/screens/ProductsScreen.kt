package com.example.loginhttp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.ProductsViewModel
import com.example.loginhttp.model.CardAction
import com.example.loginhttp.ui.components.BottomNavBar
import com.example.loginhttp.ui.components.ConfirmDeleteDialog
import com.example.loginhttp.ui.components.FabAction
import com.example.loginhttp.ui.components.FloatingButtonMenu
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.components.SearchBar
import com.example.loginhttp.ui.components.SelectionToolbar
import com.example.loginhttp.ui.components.UnifiedItemCard
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.utils.SetStatusBarColor

@Composable
fun ProductsScreen(
    selectedScreen: String = "Settings",
    onNavigate: (String) -> Unit,
) {
    val viewModel: ProductsViewModel = viewModel()

    val products by viewModel.items.collectAsState()

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
            FloatingButtonMenu(
                actions = listOf(
                    FabAction("Preuzmi", Icons.Default.Download) { viewModel.downloadItems() },
                    FabAction("Učitaj", Icons.Default.Upload) { viewModel.loadItems() },
                    FabAction("Izvoz", Icons.Default.ImportExport) { viewModel.exportItems() }
                ),
            )
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
            MenuHeader(screenWidth = screenWidth, title = "Artikli")

            if (isInSelectionMode) {
                SelectionToolbar(
                    selectedCount = selectedItems.size,
                    onSelectAll = { viewModel.selectAll(products.map { it.id }) },
                    actions = listOf(
                        Icons.Default.Delete to { viewModel.confirmDelete(selectedItems.toList()) }
                    )
                )
            }

            SearchBar(
                value = viewModel.searchQuery,
                onValueChange = viewModel::onSearchChange,
                placeholderText = "Pretraži artikle (${products.size})",
            )

            LazyColumn(
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products) { product ->
                    UnifiedItemCard(
                        id = product.id.toString(),
                        icon = null,
                        isSelected = selectedItems.contains(product.id),
                        selectionMode = isInSelectionMode,
                        onClick = {
                            if (isInSelectionMode) viewModel.toggleSelection(product.id)
                        },
                        onLongPress = {
                            viewModel.toggleSelection(product.id)
                        },
                        infoRows = buildList {
                            add("Naziv" to product.name)
                            add("Barkod" to product.barcode.toString())
                            add("Mjerna jedinica" to product.unit.displayName)
                        },
                        actions = listOf(
                            CardAction("Izbriši", Icons.Default.Delete) {
                                viewModel.confirmDelete(listOf(product.id))
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewProductsScreen() {
    ProductsScreen(
        onNavigate = {}
    )
}