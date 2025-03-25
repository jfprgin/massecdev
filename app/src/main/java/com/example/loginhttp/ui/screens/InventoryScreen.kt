package com.example.loginhttp.ui.screens
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
import com.example.loginhttp.ui.theme.Green
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White
import com.example.loginhttp.ui.utils.SetStatusBarColor

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
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DeepNavy)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Odabrano: ${selectedItems.size}",
                                color = White,
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            IconButton(
                                onClick = {
                                    viewModel.syncSelectedItems()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Sync",
                                    tint = White
                                )
                            }

                            IconButton(
                                onClick = {
                                    showDeleteConfirm = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = White
                                )
                            }
                        }

                        TextButton(
                            onClick = {
                                viewModel.clearSelection()
                            }
                        ) {
                            Text(
                                text = "Odustani",
                                color = MassecRed
                            )
                        }
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(items) { item ->
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
                            onDelete = { viewModel.deleteItem(it) },
                            onSync = { viewModel.syncItem(it) },
                            onShowTable = { /* TODO */ },
                            createdAt = viewModel.formatTimestamp(item.timestamp)
                        )
                    }
                }
            }

            if(showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    title = {
                        Text("Potvrda brisanja")
                    },
                    text = {
                        Text("Jeste li sigurni da želite izbrisati odabrane stavke?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteSelectedItems()
                                showDeleteConfirm = false
                            }
                        ) {
                            Text("Da", color = MassecRed)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDeleteConfirm = false
                            }
                        ) {
                            Text("Ne", color = MassecRed)
                        }
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
            Text("Inventura", fontSize = 20.sp, color = DeepNavy)

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
                    modifier = Modifier.fillMaxWidth().height(48.dp),
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
                    modifier = Modifier.fillMaxWidth().height(48.dp),
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
                    modifier = Modifier.fillMaxWidth().height(48.dp),
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
    onDelete: (Int) -> Unit,
    onSync: (Int) -> Unit,
    onShowTable: (Int) -> Unit,
    createdAt: String
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            )
            .border(
                width = 2.dp,
                color = if (item.isSynced) Green else MassecRed,
                shape = RoundedCornerShape(12.dp)
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
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        if (isSelected) DeepNavy else Color.Transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = if (selectionMode) {
                            if (isSelected) DeepNavy else LightGray
                        } else {
                            Color.Transparent
                        },
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

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(item.name, fontSize = 18.sp, color = DeepNavy)
                Text(createdAt, fontSize = 14.sp, color = DarkGray)
            }

            Box {
                IconButton(onClick = { menuExpanded = true}) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = DeepNavy
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(
                            "Sinhroniziraj",
                            color = DarkText
                        ) },
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
                    DropdownMenuItem(
                        text = { Text(
                            "Izbriši",
                            color = DarkText
                        ) },
                        onClick = {
                            onDelete(item.id)
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