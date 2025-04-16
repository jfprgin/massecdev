package com.example.loginhttp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.ProductsViewModel
import com.example.loginhttp.model.CardAction
import com.example.loginhttp.ui.components.BottomNavBar
import com.example.loginhttp.ui.components.ConfirmDeleteDialog
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.components.SelectionToolbar
import com.example.loginhttp.ui.components.UnifiedItemCard
import com.example.loginhttp.ui.theme.DarkGray
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White
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

    var menuExpanded by remember {  mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    BackHandler(enabled = isInSelectionMode) {
        viewModel.clearSelection()
    }

    SetStatusBarColor(color = DeepNavy, darkIcons = false)

    Scaffold(
        floatingActionButton = {
            Box(
                Modifier.padding(bottom = 8.dp, end = 8.dp)
            ) {
                FloatingActionButton(
                    onClick = { menuExpanded = true },
                    contentColor = DeepNavy,
                    containerColor = DeepNavy,
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = White
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(White)
                        .padding(8.dp),

                ) {
                    DropdownMenuItem(
                        text = { Text("Preuzmi") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = "Download",
                                tint = DeepNavy
                            )
                        },
                        onClick = {
                            viewModel.downloadItems()
                            menuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Učitaj") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Upload,
                                contentDescription = "Upload",
                                tint = DeepNavy
                            )
                        },
                        onClick = {
                            viewModel.loadItems()
                            menuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Izvoz") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.ImportExport,
                                contentDescription = "Export",
                                tint = DeepNavy
                            )
                        },
                        onClick = {
                            viewModel.exportItems()
                            menuExpanded = false
                        }
                    )
                }
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

            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = viewModel::onSearchChange,
                placeholder = {
                    Text(
                        "Pretraži artikle (${products.size})",
                        color = DarkGray,
                        fontSize = 18.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = DeepNavy
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = DeepNavy,
                    focusedIndicatorColor = MassecRed,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                ),
                singleLine = true,
                textStyle = TextStyle(fontSize = 16.sp)
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
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