package com.example.loginhttp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.LocationsViewModel
import com.example.loginhttp.model.LocationItem
import com.example.loginhttp.ui.components.BottomNavBar
import com.example.loginhttp.ui.components.MenuHeader
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.White
import com.example.loginhttp.ui.utils.SetStatusBarColor

@Composable
fun LocationsScreen(
    selectedScreen: String = "Settings",
    onNavigate: (String) -> Unit,
) {
    val viewModel: LocationsViewModel = viewModel()

    val locations by viewModel.locations.collectAsState()
    val selectedItems by viewModel.selectedItems.collectAsState()

    val isInSelectionMode= selectedItems.isNotEmpty()
    val showDeleteConfirm by remember { mutableStateOf<LocationItem?>(null) }
    val context = LocalContext.current

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    SetStatusBarColor(color = DeepNavy, darkIcons = false)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.downloadLocations()},
                contentColor = DeepNavy,
                containerColor = DeepNavy,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Download,
                    contentDescription = "Download",
                    tint = White
                    )
            }
        },

        bottomBar = {
            BottomNavBar(selectedScreen = selectedScreen, onNavigate = onNavigate)
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightGray)
        ) {
            MenuHeader(screenWidth = screenWidth, title = "Lokacije")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LocationsScreenPreview() {
    LocationsScreen(
        onNavigate = {},
    )
}