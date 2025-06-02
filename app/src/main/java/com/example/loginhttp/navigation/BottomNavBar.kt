package com.example.loginhttp.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.loginhttp.ui.theme.*
import com.example.loginhttp.navigation.utils.RouteUtils.isTopLevelRouteSelected

@Composable
fun BottomNavBar(navController: NavHostController) {

    val navigationScreen = listOf(
        AppScreen.Main.Home,
        AppScreen.Main.Inventory,
        AppScreen.Main.Warehouse,
        AppScreen.Main.Settings
    )
    NavigationBar(
        containerColor = White,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        navigationScreen.forEach { item ->
            NavigationBarItem(
                selected = isTopLevelRouteSelected(currentRoute, item.route),
                label = {
                    Text(
                        text = stringResource(id = item.title!!),
                        // style = MaterialTheme.typography.displaySmall
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon!!,
                        contentDescription = stringResource(id = item.title!!)
                    )
                },
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when reselecting the same item
                            launchSingleTop = true
                            restoreState = true
                        }
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
            navController = NavHostController(context = LocalContext.current)
        )
    }
}
