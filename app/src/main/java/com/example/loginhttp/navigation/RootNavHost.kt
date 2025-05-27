package com.example.loginhttp.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.loginhttp.InventoryViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RootNavHost(isAuthenticated: Boolean) {
    SharedTransitionLayout {
        val topAppbarTitle = remember { mutableStateOf("") }
//        val topAppBarState = rememberTopAppBarState()
//        val barScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(state = topAppBarState)

        val showTopBarState = rememberSaveable { (mutableStateOf(true)) }
        val showBottomBarState = rememberSaveable { (mutableStateOf(true)) }

        val fabContent = remember { mutableStateOf<@Composable (() -> Unit)?>(null) }

//        val coroutineScope = rememberCoroutineScope()
        val rootNavHostController = rememberNavController()
        val rootNavBackStackEntry by rootNavHostController.currentBackStackEntryAsState()

        val inventoryViewModel: InventoryViewModel = viewModel()

        // Control TopBar and BottomBar
        when (rootNavBackStackEntry?.destination?.route) {
            AppScreen.Main.Home.route -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(AppScreen.Main.Home.title!!)
            }

            AppScreen.Main.Inventory.route -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(AppScreen.Main.Inventory.title!!)
            }

            AppScreen.Main.Warehouse.route -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(AppScreen.Main.Warehouse.title!!)
            }

            AppScreen.Main.Settings.route -> {
                showBottomBarState.value = true
                showTopBarState.value = true
                topAppbarTitle.value = stringResource(AppScreen.Main.Settings.title!!)
            }

            else -> {
                showBottomBarState.value = false
                showTopBarState.value = false
            }
        }

        Scaffold(
            // TODO: topbar
            topBar = {
                if (showTopBarState.value) {
//                    AppTopBar(
//                        topAppbarTitle.value,
//                        barScrollBehavior,
//                    )
                }
            },
            bottomBar = {
                if (showBottomBarState.value) {
                    BottomNavBar(navController = rootNavHostController)
                }
            },
            floatingActionButton = fabContent.value ?: {},
        ) {
            Column(
                modifier = Modifier
                    // Padding should be equal to the bottom bar height
                    .padding(bottom = 56.dp)
            ) {
                NavHost(
                    navController = rootNavHostController,
                    startDestination = if (isAuthenticated) AppScreen.Main.route else AppScreen.Auth.route,
                    enterTransition = {
                        EnterTransition.None
                    },
                    exitTransition = {
                        ExitTransition.None
                    }
                ) {
                    authNavGraph(
                        rootNavHostController
                    )
                    mainNavGraph(
                        rootNavHostController,
                        rootNavBackStackEntry,
                        fabContent = fabContent,
                        inventoryViewModel,
                    )
                }
            }
        }
    }
}