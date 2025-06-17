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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginhttp.SettingsViewModel
import com.example.loginhttp.navigation.AppRoutes
import com.example.loginhttp.navigation.BottomNavBar
import com.example.loginhttp.navigation.UnifiedFloatingActionButton
import com.example.loginhttp.navigation.UnifiedTopAppBar
import com.example.loginhttp.ui.menu.settingsItems
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White
import com.example.loginhttp.ui.utils.SetStatusBarColor

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onItemClick: (String) -> Unit,
) {
    val isRefreshing = viewModel.isRefreshing.collectAsState().value
    val lastSync = viewModel.getLastSyncedText()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refreshDatabase() }
    )

    SetStatusBarColor(color = DeepNavy, darkIcons = false)

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
//                .padding(innerPadding)
                .background(LightGray)
        ) {
            // HEADER
//            MenuHeader(screenWidth = screenWidth, title = "Postavke")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Last sync info
                Text(
                    text = lastSync,
                    color = DarkText,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )

                settingsItems.forEach { item ->
                    SettingsItemCard(
                        title = stringResource(item.titleRes),
                        icon = item.icon,
                        onClick = { onItemClick(item.route) }
                    )
                }
            }

            // Pull-to-refresh indicator
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = MassecRed
            )
        }
    }
}

@Composable
fun SettingsItemCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSettingsScreen() {
    val mockViewModel: SettingsViewModel = viewModel()

    val mockFAB = @Composable {
        UnifiedFloatingActionButton(
            icon = Icons.Default.Refresh,
            contentDescription = "Refresh",
            onClick = { mockViewModel.refreshDatabase() }
        )
    }
    Scaffold(
        topBar = {
            UnifiedTopAppBar(
                title = "Settings",
            )
        },

        bottomBar = {
            BottomNavBar(
                selectedTab = AppRoutes.SETTINGS,
                onTabSelected = {}
            )
        },
        floatingActionButton = mockFAB
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SettingsScreen(
                viewModel = mockViewModel,
                onItemClick = {},
            )
        }
    }
}
