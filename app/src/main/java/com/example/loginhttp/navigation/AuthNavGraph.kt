package com.example.loginhttp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.loginhttp.ui.screens.LoginScreen

fun NavGraphBuilder.authNavGraph(
    navHostController: NavHostController
) {
    navigation(
        startDestination = AppScreen.Auth.Login.route,
        route = AppScreen.Auth.route
    ) {
        composable(
            route = AppScreen.Auth.Login.route
        ) {
            LoginScreen(
                onLoginSuccess = {      // Maybe change to navigateToHome
                    navHostController.navigate(AppScreen.Main.route) {
                        popUpTo(AppScreen.Auth.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}