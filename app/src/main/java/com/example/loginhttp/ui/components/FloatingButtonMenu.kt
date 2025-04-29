package com.example.loginhttp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.White

@Composable
fun FloatingButtonMenu(
    actions: List<FabAction>? = null,
    onAddClick: (() -> Unit)? = null,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        Modifier.padding(bottom = 16.dp, end = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Add FAB
            if (onAddClick != null) {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = DeepNavy,
                    contentColor = White,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }

            // Menu FAB
            if (!actions.isNullOrEmpty()) {
                Box {
                    FloatingActionButton(
                        onClick = { menuExpanded = true },
                        containerColor = DeepNavy,
                        contentColor = White,
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp),
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier
                            .background(White)
                            .padding(8.dp)
                    ) {
                        actions.forEach { action ->
                            DropdownMenuItem(
                                text = { Text(action.label) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = action.icon,
                                        contentDescription = action.label,
                                        tint = DeepNavy
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    action.onClick()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

data class FabAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)