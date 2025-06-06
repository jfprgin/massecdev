package com.example.loginhttp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.loginhttp.features.auth.navigation.authNavGraph

@Composable
fun RootNavHost(isAuthenticated: Boolean) {

    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = if (isAuthenticated) AppRoutes.MAIN else AppRoutes.AUTH,
    ) {
        // Authentication navigation graph
        authNavGraph(rootNavController)

        // Main navigation graph
        composable(AppRoutes.MAIN) {
            MainAppHost()
        }
    }
}

