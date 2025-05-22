package com.example.loginhttp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.ItemManagementViewModel
import com.example.loginhttp.model.CardAction
import com.example.loginhttp.model.CatalogItem
import com.example.loginhttp.ui.components.BottomNavBar
import com.example.loginhttp.ui.components.ConfirmDeleteDialog
import com.example.loginhttp.ui.components.FloatingButtonMenu
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.components.SearchBar
import com.example.loginhttp.ui.components.SelectionToolbar
import com.example.loginhttp.ui.components.UnifiedItemCard
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.White
import com.example.loginhttp.ui.utils.SetStatusBarColor

@Composable
fun ItemManagementScreen(
    selectedScreen: String, // TODO: Define the type of selectedScreen
    onNavigate: (String) -> Unit,
) {
    val viewModel: ItemManagementViewModel = viewModel()

    val items by viewModel.items.collectAsState()

    val selectedItems by viewModel.selectedItems.collectAsState()
    val isInSelectionMode = selectedItems.isNotEmpty()

    val pendingDeleteIds by viewModel.pendingDeleteIds.collectAsState()

    val inlineSearchQuery = viewModel.inlineSearchQuery
    val isAddItemDialogOpen = viewModel.isAddItemDialogOpen
    val catalogSearchResults by viewModel.catalogSearchResults.collectAsState()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWith = LocalConfiguration.current.screenWidthDp.dp

    BackHandler(enabled = isInSelectionMode) {
        viewModel.clearSelection()
    }

    SetStatusBarColor(color = DeepNavy, darkIcons = false)

    Scaffold(
        floatingActionButton = {
            FloatingButtonMenu(
                onAddClick = {
                    viewModel.openAddItemDialog()
                }
            )
        },

//        bottomBar = {
//            BottomNavBar(selectedScreen = selectedScreen, onNavigate = onNavigate)
//        }
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
                    placeholderText = "Pretraži (${items.size})",
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
                            infoRows = listOfNotNull(
                                null to item.name,
                                item.code?.let { "Šifra" to it },
                                item.unitOfMeasure?.let { "Mjerna jedinica" to it },
                            ),
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

            if (isAddItemDialogOpen) {
                AddItemDialog(
                    searchQuery = viewModel.addItemSearchQuery,
                    onSearchChange = viewModel::onAddItemSearchChange,
                    searchResults = catalogSearchResults,
                    onItemSelected = { item ->
                        viewModel.addItem(item)
                    },
                    onDismiss = viewModel::closeAddItemDialog,
                )
            }
        }
    }
}

@Composable
fun AddItemDialog(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    searchResults: List<CatalogItem>,
    onItemSelected: (CatalogItem) -> Unit,
    onDismiss: () -> Unit,
) {
    val dialogHeight = LocalConfiguration.current.screenHeightDp.dp * 0.7f
    val dialogWidth = LocalConfiguration.current.screenWidthDp.dp * 0.9f

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        )
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 8.dp,
            modifier = Modifier
                .widthIn(max = dialogWidth)
                .heightIn(max = dialogHeight)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(LightGray)
                    .padding(16.dp)
            ) {
                SearchBar(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholderText = "Unesite naziv, barkod ili šifru",
                    backgroundColor = LightGray,
                    searchBarColor = White,
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightGray)
                ) {
                    items(searchResults) { item ->
                        CatalogItemRow(
                            item = item,
                            onClick = {
                                onItemSelected(item)
                                onDismiss()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CatalogItemRow(item: CatalogItem, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = White,
        shadowElevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DeepNavy,
                )

            Spacer(modifier = Modifier.height(8.dp))

            item.code?.let {
                Text(
                    text = "Šifra: $it",
                    fontSize = 16.sp,
                    color = DarkText,
                )
            }
            item.barcode?.let {
                Text(
                    text = "Barkod: $it",
                    fontSize = 16.sp,
                    color = DarkText
                )
            }
            item.unitOfMeasure?.let {
                Text(
                    text = "Mjerna jedinica: $it",
                    fontSize = 16.sp,
                    color = DarkText
                )
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