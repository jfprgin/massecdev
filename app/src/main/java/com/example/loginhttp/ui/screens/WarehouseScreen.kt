package com.example.loginhttp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginhttp.model.WarehouseItem
import com.example.loginhttp.ui.components.BottomNavBar
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White
import com.example.loginhttp.ui.utils.SetStatusBarColor

@Composable
fun WarehouseScreen(
    selectedScreen: String = "Warehouse",
    onNavigate: (String) -> Unit,
    onItemClick: (String) -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    SetStatusBarColor(color = DeepNavy, darkIcons = false)

    Scaffold (
        bottomBar = {
            BottomNavBar(selectedScreen = selectedScreen, onNavigate = onNavigate)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightGray)
        ) {
            // HEADER
            MenuHeader(screenWidth = screenWidth, title = "Skladište")

            Spacer(modifier = Modifier.height(12.dp))

            // ITEM LIST
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                warehouseItems.forEach { item ->
                    WarehouseItemCard(
                        title = item.title,
                        icon = item.icon,
                        onClick = { onItemClick(item.title) }
                    )
                }
            }
        }
    }
}

@Composable
fun WarehouseItemCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = MassecRed,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                color = DarkText
            )
        }
    }
}

// Example data
val warehouseItems = listOf(
    WarehouseItem("Prijem robe", Icons.Default.MoveToInbox),
    WarehouseItem("Izdavanje robe", Icons.Default.Outbox),
    WarehouseItem("Prijenos robe", Icons.Default.Sync),
    WarehouseItem("Povrat robe", Icons.AutoMirrored.Filled.Undo),
    WarehouseItem("Otpis robe", Icons.Default.Delete),
    WarehouseItem("Naručivanje robe", Icons.Default.LocalShipping),
    WarehouseItem("Virtualno skladište", Icons.Default.Warehouse),
    WarehouseItem("Predlošci", Icons.AutoMirrored.Filled.List)
)

@Preview
@Composable
fun WarehouseScreenPreview() {
    WarehouseScreen(selectedScreen = "Warehouse", onNavigate = {}, onItemClick = {})
}
