package com.example.loginhttp.navigation.utils

import com.example.loginhttp.navigation.AppScreen
import com.example.loginhttp.navigation.WarehouseRoutes

object RouteUtils {

    fun isTopLevelRouteSelected(currentRoute: String?, topLevelRoute: String): Boolean {
        return when (topLevelRoute) {
            AppScreen.Main.Home.route -> currentRoute == AppScreen.Main.Home.route

            AppScreen.Main.Inventory.route -> currentRoute == AppScreen.Main.Inventory.route

            AppScreen.Main.Warehouse.route -> currentRoute == AppScreen.Main.Warehouse.route ||
                currentRoute in WarehouseRoutes.all

            AppScreen.Main.Settings.route -> currentRoute == AppScreen.Main.Settings.route

            else -> false
        }
    }
}