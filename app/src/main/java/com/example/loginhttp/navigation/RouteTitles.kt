package com.example.loginhttp.navigation

import com.example.loginhttp.R

val routeTitleMap = mapOf(
    // Tabs
    AppRoutes.HOME to R.string.home_title,
    AppRoutes.INVENTORY to R.string.inventory_title,
    AppRoutes.WAREHOUSE to R.string.warehouse_title,
    AppRoutes.SETTINGS to R.string.settings_title,

    // Warehouse screens
    WarehouseRoutes.RECEIPT_OF_GOODS to R.string.receipt_of_goods_title,
    WarehouseRoutes.ISSUING_GOODS to R.string.issuing_goods_title,
    WarehouseRoutes.TRANSFER_OF_GOODS to R.string.transfer_of_goods_title,
    WarehouseRoutes.RETURN_OF_GOODS to R.string.return_of_goods_title,
    WarehouseRoutes.WRITE_OFF_OF_GOODS to R.string.write_off_of_goods_title,
    WarehouseRoutes.ORDERING_GOODS to R.string.ordering_goods_title,
    WarehouseRoutes.VIRTUAL_WAREHOUSE to R.string.virtual_warehouse_title,
    WarehouseRoutes.TEMPLATES to R.string.templates_title,

    // Settings screens
    SettingsRoutes.PRODUCTS to R.string.products_title,
    SettingsRoutes.SUPPLIERS to R.string.suppliers_title,
    SettingsRoutes.WAREHOUSES to R.string.warehouses_title,
    SettingsRoutes.COST_CENTERS to R.string.cost_centers_title,
    SettingsRoutes.LOCATIONS to R.string.locations_title,
    SettingsRoutes.INVENTORY_LISTS to R.string.inventory_lists_title,
    SettingsRoutes.INVENTORY_GROUPS to R.string.inventory_groups_title
)