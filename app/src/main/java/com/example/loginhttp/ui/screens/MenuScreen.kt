package com.example.loginhttp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.loginhttp.ui.theme.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.utils.SetStatusBarColor

@Composable
fun MenuScreen(onMenuClick: (String) -> Unit) {

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    SetStatusBarColor(color = DeepNavy, darkIcons = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        // HEADER
        MenuHeader(screenWidth = screenWidth, title = "Izbornik")

        // MENU ITEMS
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MenuItemCard(
                title = "Inventura",
                icon = Icons.Default.Inventory,
                screenHeight = screenHeight,
                screenWidth = screenWidth,
                onClick = { onMenuClick("Inventory") }
            )
            MenuItemCard(
                title = "Skladište",
                icon = Icons.Default.Warehouse,
                screenHeight = screenHeight,
                screenWidth = screenWidth,
                onClick = { onMenuClick("Warehouse") }
            )
            MenuItemCard(
                title = "Postavke",
                icon = Icons.Default.Settings,
                screenHeight = screenHeight,
                screenWidth = screenWidth,
                onClick = { onMenuClick("Settings") }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MenuItemCard(
    title: String,
    icon: ImageVector,
    screenHeight: Dp,
    screenWidth: Dp,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight * 0.26f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = MassecRed,
                modifier = Modifier.size(screenWidth * 0.24f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = (screenWidth.value * 0.06f).sp,
                color = DarkText
            )
        }
    }
}

// Preview this composable
@Preview
@Composable
fun MenuScreenPreview() {
    LoginHTTPTheme {
        MenuScreen(onMenuClick = {})
    }
}
