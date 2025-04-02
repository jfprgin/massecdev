package com.example.loginhttp.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.ReceiptOfGoodsViewModel

@Composable
fun ReceiptOfGoodsScreen(
    selectedScreen: String = "Warehouse",
    onNavigate: (String) -> Unit = {},
) {
    val viewModel: ReceiptOfGoodsViewModel = viewModel()


}