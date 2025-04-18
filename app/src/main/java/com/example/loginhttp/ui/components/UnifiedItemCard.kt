package com.example.loginhttp.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginhttp.model.CardAction
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UnifiedItemCard(
    id: String? = null,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = DeepNavy,
    isSynced: Boolean? = null,
    isSelected: Boolean = false,
    selectionMode: Boolean = false,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    infoRows: List<Pair<String?, String>>,
    actions: List<CardAction>,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val interaction = if (onLongPress != null) {
        Modifier.combinedClickable(onClick = onClick, onLongClick = onLongPress)
    } else {
        Modifier.clickable(onClick = onClick)
    }

    val idBackground = when (isSynced) {
        true -> DeepNavy
        false -> MassecRed
        else -> DeepNavy
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .then(interaction),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) LightGray else White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left-side icon
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (id != null) {
                    // ID label
                    Box(
                        modifier = Modifier
                            .background(idBackground, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = id,
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Info rows
                infoRows.forEach { (label, value) ->
                    val text = if (!label.isNullOrBlank()) "$label: $value" else value
                    Text(
                        text,
                        fontSize = 18.sp,
                        color = DarkText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Right-side actions
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(32.dp),
            ) {
                when {
                    selectionMode -> {
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
                    }

                    actions.size == 1 -> {
                        val action = actions.first()
                        IconButton(onClick = action.onClick) {
                            Icon(
                                action.icon,
                                contentDescription = action.label,
                                tint = DeepNavy
                            )
                        }
                    }

                    actions.size > 1 -> {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = DeepNavy
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            containerColor = White
                        ) {
                            actions.forEach { action ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            action.label,
                                            color = DarkText,
                                            fontSize = 14.sp,
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            action.icon,
                                            contentDescription = action.label,
                                            tint = DeepNavy
                                        )
                                    },
                                    onClick = {
                                        action.onClick()
                                        menuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}