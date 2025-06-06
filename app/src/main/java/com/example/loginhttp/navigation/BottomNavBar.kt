package com.example.loginhttp.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.loginhttp.ui.theme.*

@Composable
fun BottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
) {

    val tabs = listOf(
        AppScreen.Main.Home,
        AppScreen.Main.Inventory,
        AppScreen.Main.Warehouse,
        AppScreen.Main.Settings
    )

    NavigationBar(containerColor = White) {
        tabs.forEach { item ->
            val isSelected = selectedTab == item.route

            NavigationBarItem(
                selected = isSelected,
                label = {
                    Text(text = stringResource(id = item.title!!))
                },
                icon = {
                    Icon(
                        imageVector = item.icon!!,
                        contentDescription = stringResource(id = item.title!!)
                    )
                },
                onClick = {
                    if (!isSelected) {
                        onTabSelected(item.route)
                    }
                },
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
}

@Preview
@Composable
fun BottomNavBarPreview() {
    LoginHTTPTheme {
        BottomNavBar(
            selectedTab = AppScreen.Main.Home.route,
            onTabSelected = {}
        )
    }
}
