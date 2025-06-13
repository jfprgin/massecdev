package com.example.loginhttp.features.warehouse.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.features.warehouse.viewmodel.TemplatesViewModel
import com.example.loginhttp.model.CardAction
import com.example.loginhttp.navigation.AppRoutes
import com.example.loginhttp.navigation.BottomNavBar
import com.example.loginhttp.navigation.UnifiedFAB
import com.example.loginhttp.ui.components.BottomSheet
import com.example.loginhttp.ui.components.FieldType
import com.example.loginhttp.ui.components.FormField
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.components.UnifiedItemCard
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.utils.SetStatusBarColor

@Composable
fun TemplatesScreen(viewModel: TemplatesViewModel) {

    val templates by viewModel.items.collectAsState()
    val isSheetVisible by viewModel.isSheetVisible.collectAsState()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    SetStatusBarColor(color = DeepNavy, darkIcons = false)

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightGray)
        ) {
            MenuHeader(screenWidth = screenWidth, title = "Predlošci")

            LazyColumn(
                contentPadding = PaddingValues(
                    top = 8.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(templates) { template ->
                    UnifiedItemCard(
                        id = template.id.toString(),
                        icon = null,
                        infoRows = listOf(
                            null to template.name,
                        ),
                        onClick = { },
                        actions = listOf(
                            CardAction("Izbriši", Icons.Default.Delete) {
                                viewModel.confirmDelete(listOf(template.id))
                            }
                        ),
                    )
                }
            }
        }

        if (isSheetVisible) {
            val fields = listOf(
                FormField(
                    label = "Naziv predloška",
                    FieldType.TEXT,
                )
            )

            BottomSheet(
                title = "Novi predložak",
                fields = fields,
                onDismiss = { viewModel.toggleSheet(false) },
                onSubmit = { values ->
                    val name = values[0] as String
                    viewModel.addItem(name)
                    viewModel.toggleSheet(false)
                },
            )
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TemplatesScreenPreview() {

    val mockViewModel: TemplatesViewModel = viewModel()

    val mockFAB = @Composable {
        UnifiedFAB(
            icon = Icons.Default.Add,
            contentDescription = "Add",
            onClick = { mockViewModel.toggleSheet(true) }
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = AppRoutes.WAREHOUSE,
                onTabSelected = { /* Handle tab selection */ },
            )
        },
        floatingActionButton = mockFAB,
    ) {
        TemplatesScreen(viewModel = mockViewModel)
    }
}