package com.example.loginhttp.model

import androidx.compose.ui.graphics.vector.ImageVector

data class CardAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)
