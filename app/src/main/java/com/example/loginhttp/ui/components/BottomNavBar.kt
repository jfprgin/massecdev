package com.example.loginhttp.ui.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.loginhttp.ui.theme.*

@Composable
fun BottomNavBar(selectedScreen: String, onNavigate: (String) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 6.dp        // Slight shadow
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Inventory, contentDescription = "Inventura") },
            label = { Text("Inventura") },
            selected = selectedScreen == "Inventory",
            onClick = { onNavigate("Inventory") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MassecRed.copy(alpha = 0.1f),
                selectedIconColor = MassecRed,
                unselectedIconColor = DeepNavy,
                selectedTextColor = MassecRed,
                unselectedTextColor = DarkText
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Warehouse, contentDescription = "Skladište") },
            label = { Text("Skladište") },
            selected = selectedScreen == "Warehouse",
            onClick = { onNavigate("Warehouse") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MassecRed.copy(alpha = 0.1f),
                selectedIconColor = MassecRed,
                unselectedIconColor = DeepNavy,
                selectedTextColor = MassecRed,
                unselectedTextColor = DarkText
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Postavke") },
            label = { Text("Postavke") },
            selected = selectedScreen == "Settings",
            onClick = { onNavigate("Settings") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MassecRed.copy(alpha = 0.1f),
                selectedIconColor = MassecRed,
                unselectedIconColor = DeepNavy,
                selectedTextColor = MassecRed,
                unselectedTextColor = DarkText
            )
        )
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    LoginHTTPTheme {
        BottomNavBar(selectedScreen = "Inventory", onNavigate = {})
    }
}
