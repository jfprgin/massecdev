package com.example.loginhttp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginhttp.navigation.AppRoutes
import com.example.loginhttp.navigation.BottomNavBar
import com.example.loginhttp.navigation.UnifiedTopAppBar
import com.example.loginhttp.ui.menu.warehouseItems
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White
import com.example.loginhttp.ui.utils.SetStatusBarColor

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WarehouseScreen(
    onItemClick: (String) -> Unit
) {
    SetStatusBarColor(color = DeepNavy, darkIcons = false)

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGray)
        ) {
//            // HEADER
//            MenuHeader(screenWidth = screenWidth, title = "Skladište")

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
                        title = stringResource(item.titleRes),
                        icon = item.icon,
                        onClick = { onItemClick(item.route) }
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

@Preview
@Composable
fun WarehouseScreenPreview() {
    Scaffold(
        topBar = {
            UnifiedTopAppBar(
                title = "Warehouse",
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = AppRoutes.WAREHOUSE,
                onTabSelected = {}
            )
        },
    ){ innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            WarehouseScreen(onItemClick = {})
        }
    }
}
