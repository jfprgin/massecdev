package com.example.loginhttp.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
//    navigationIcon: @Composable (() -> Unit),
//    actions: @Composable (RowScope.() -> Unit) = {  }
) {
    TopAppBar(
        title = { Text(text = title) },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = White,
                    disabledContentColor = White.copy(alpha = 0.38f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu Icon"
                )
            }
        },
        actions = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DeepNavy,
            titleContentColor = White,
            navigationIconContentColor = White
        )
    )
}

@Preview(showBackground = true)
@Composable
fun UnifiedTopAppBarPreview() {
    UnifiedTopAppBar(
        title = "Unified Top App Bar",
//        navigationIcon = {
//            IconButton(
//                onClick = {},
//                colors = IconButtonDefaults.iconButtonColors(
//                    contentColor = White,
//                    disabledContentColor = White.copy(alpha = 0.38f)
//                )
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Menu,
//                    contentDescription = "Menu Icon"
//                )
//            }
//        },
//        actions = { /* Add your actions here */ }
    )
}