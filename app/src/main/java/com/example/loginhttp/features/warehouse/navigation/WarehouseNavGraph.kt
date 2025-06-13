package com.example.loginhttp.features.warehouse.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.loginhttp.features.warehouse.screens.IssuingGoodsScreen
import com.example.loginhttp.features.warehouse.screens.OrderingGoodsScreen
import com.example.loginhttp.features.warehouse.screens.ReceiptOfGoodsScreen
import com.example.loginhttp.features.warehouse.screens.ReturnOfGoodsScreen
import com.example.loginhttp.features.warehouse.screens.TemplatesScreen
import com.example.loginhttp.features.warehouse.screens.TransferOfGoodsScreen
import com.example.loginhttp.features.warehouse.screens.VirtualWarehouseScreen
import com.example.loginhttp.features.warehouse.screens.WriteOffOfGoodsScreen
import com.example.loginhttp.features.warehouse.viewmodel.IssuingGoodsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.OrderingGoodsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.ReceiptOfGoodsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.ReturnOfGoodsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.TemplatesViewModel
import com.example.loginhttp.features.warehouse.viewmodel.TransferOfGoodsViewModel
import com.example.loginhttp.features.warehouse.viewmodel.VirtualWarehouseViewModel
import com.example.loginhttp.features.warehouse.viewmodel.WriteOffOfGoodsViewModel
import com.example.loginhttp.navigation.AppRoutes
import com.example.loginhttp.navigation.UnifiedFAB
import com.example.loginhttp.navigation.WarehouseRoutes
import com.example.loginhttp.ui.screens.WarehouseScreen

fun NavGraphBuilder.warehouseNavGraph(
    navHostController: NavHostController,
    fabContent: MutableState<(@Composable () -> Unit)?>,
    viewModels : WarehouseViewModels
) {
    composable(AppRoutes.WAREHOUSE) {
        fabContent.value = null
        WarehouseScreen(onItemClick = navHostController::navigate)
    }

    composable(WarehouseRoutes.RECEIPT_OF_GOODS) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { viewModels.receiptOfGoodsViewModel.toggleSheet(true) }
            )
        }
        ReceiptOfGoodsScreen(viewModel = viewModels.receiptOfGoodsViewModel)
    }

    composable(WarehouseRoutes.ISSUING_GOODS) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { viewModels.issuingGoodsViewModel.toggleSheet(true) }
            )
        }
        IssuingGoodsScreen(viewModel = viewModels.issuingGoodsViewModel)
    }

    composable(WarehouseRoutes.TRANSFER_OF_GOODS) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { viewModels.transferOfGoodsViewModel.toggleSheet(true) }
            )
        }
        TransferOfGoodsScreen(viewModel = viewModels.transferOfGoodsViewModel)
    }

    composable(WarehouseRoutes.RETURN_OF_GOODS) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { viewModels.returnOfGoodsViewModel.toggleSheet(true) }
            )
        }
        ReturnOfGoodsScreen(viewModel = viewModels.returnOfGoodsViewModel)
    }

    composable(WarehouseRoutes.WRITE_OFF_OF_GOODS) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { viewModels.writeOffOfGoodsViewModel.toggleSheet(true) }
            )
        }
        WriteOffOfGoodsScreen(viewModel = viewModels.writeOffOfGoodsViewModel)
    }

    composable(WarehouseRoutes.ORDERING_GOODS) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { viewModels.orderingGoodsViewModel.toggleSheet(true) }
            )
        }
        OrderingGoodsScreen(viewModel = viewModels.orderingGoodsViewModel)
    }

    composable(WarehouseRoutes.VIRTUAL_WAREHOUSE) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Refresh,
                contentDescription = "Refresh",
                onClick = { viewModels.virtualWarehouseViewModel.toggleSheet(true) }
            )
        }
        VirtualWarehouseScreen(viewModel = viewModels.virtualWarehouseViewModel)
    }

    composable(WarehouseRoutes.TEMPLATES) {
        fabContent.value = {
            UnifiedFAB(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = { viewModels.templatesViewModel.toggleSheet(true) }
            )
        }
        TemplatesScreen(viewModel = viewModels.templatesViewModel)
    }
}

data class WarehouseViewModels(
    val receiptOfGoodsViewModel: ReceiptOfGoodsViewModel,
    val issuingGoodsViewModel: IssuingGoodsViewModel,
    val transferOfGoodsViewModel: TransferOfGoodsViewModel,
    val returnOfGoodsViewModel: ReturnOfGoodsViewModel,
    val writeOffOfGoodsViewModel: WriteOffOfGoodsViewModel,
    val orderingGoodsViewModel: OrderingGoodsViewModel,
    val virtualWarehouseViewModel: VirtualWarehouseViewModel,
    val templatesViewModel: TemplatesViewModel
)