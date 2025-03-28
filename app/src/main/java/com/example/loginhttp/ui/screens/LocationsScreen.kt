package com.example.loginhttp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.LocationsViewModel
import com.example.loginhttp.model.LocationItem
import com.example.loginhttp.ui.components.BottomNavBar
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.theme.DarkGray
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White
import com.example.loginhttp.ui.utils.SetStatusBarColor

@Composable
fun LocationsScreen(
    selectedScreen: String = "Settings",
    onNavigate: (String) -> Unit,
) {
    val viewModel: LocationsViewModel = viewModel()

    val locations by viewModel.locations.collectAsState()
    val selectedItems by viewModel.selectedItems.collectAsState()

    val isInSelectionMode= selectedItems.isNotEmpty()
    val showDeleteConfirm = remember { mutableStateOf<LocationItem?>(null) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    BackHandler(enabled = isInSelectionMode) {
        viewModel.clearSelection()
    }

    SetStatusBarColor(color = DeepNavy, darkIcons = false)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.downloadLocations()},
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
            MenuHeader(screenWidth = screenWidth, title = "Lokacije")

            // Selection toolbar
            if (isInSelectionMode) {
                LocationsSelectionToolbar(
                    selectedCount = selectedItems.size,
                    onDelete = { viewModel.deleteSelected() },
                    onSelectAll = { viewModel.selectAll(locations.map { it.id })}
                )
            }

            // Search
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = viewModel::onSearchChange,
                placeholder = {
                    Text(
                        "Pretraži lokacije (${locations.size})",
                        color = DarkGray,
                        fontSize = 16.sp
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
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MassecRed,
                    unfocusedBorderColor = DeepNavy,
                    backgroundColor = White
                ),
                singleLine = true,
                textStyle = TextStyle(fontSize = 16.sp)
            )

            // List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(locations) { location ->
                    LocationItemCard(
                        item = location,
                        isSelected = selectedItems.contains(location.id),
                        selectionMode = isInSelectionMode,
                        onClick = {
                            if (isInSelectionMode) viewModel.toggleSelection(location.id)
                        },
                        onLongPress = {
                            viewModel.toggleSelection(location.id)
                        },
                        onDelete = { showDeleteConfirm.value = location}
                    )
                }
            }

            // Delete confirmation dialog
            showDeleteConfirm.value?.let { item ->
                AlertDialog(
                    title = {
                        Text("Potvrda brisanja")
                    },
                    text = {
                        Text("Jeste li sigurni da želite izbrisati odabrane stavke?")
                    },
                    onDismissRequest = { showDeleteConfirm.value = null },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteItem(item.id)
                            showDeleteConfirm.value = null
                        }) { Text("Da", color = MassecRed) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm.value = null }) {
                            Text("Ne", color = MassecRed)
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun LocationsSelectionToolbar(
    selectedCount: Int,
    onDelete: () -> Unit,
    onSelectAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepNavy)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 0.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.padding(top = 0.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Odabrano: $selectedCount",
                    color = White,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.width(16.dp))

                TextButton(
                    onClick = onSelectAll,
                    colors = ButtonColors(
                        containerColor = White,
                        contentColor = DarkText,
                        disabledContainerColor = DarkGray,
                        disabledContentColor = White
                    ),
                ) {
                    Text("Odaberi sve", color = DarkText)
                }
            }

            Row(
                modifier = Modifier.padding(top = 0.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDelete) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocationItemCard(
    item: LocationItem,
    isSelected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onDelete: () -> Unit
) {
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal =16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.id.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy,
                    modifier = Modifier.width(50.dp)
                )

                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    color = DarkText,
                    modifier = Modifier.weight(1f)
                )

            }

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
            } else {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MassecRed
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LocationsScreenPreview() {
    LocationsScreen(
        onNavigate = {},
    )
}