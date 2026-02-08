package com.example.loginhttp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.tooling.preview.Preview
import com.example.loginhttp.core.bluetooth.BluetoothTestScreen
import com.example.loginhttp.navigation.RootNavHost
import com.example.loginhttp.ui.theme.LoginHTTPTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private var isAuthenticated = false

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        isAuthenticated = mainViewModel.isAuthenticated()
        setContent {
            LoginHTTPTheme {
                MainScreenContent(isAuthenticated)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MainScreenContent(
    isAuthenticated: Boolean
) {
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this
        ) {
            RootNavHost(isAuthenticated)
//                BluetoothTestScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMainScreenContent() {
    LoginHTTPTheme {
        MainScreenContent(isAuthenticated = true)
    }
}