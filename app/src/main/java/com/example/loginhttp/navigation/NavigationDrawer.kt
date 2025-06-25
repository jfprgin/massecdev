package com.example.loginhttp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawer(
    scope: CoroutineScope,
    drawerState: DrawerState,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = White,
                drawerContentColor = DeepNavy
            ) {
                AppDrawerContent(
                    companyName = "Your Company Name",
                    username = "User Name",
                    licenseType = "Premium",
                    licenseExpiresIn = "30 days",
                    appVersion = "v1.0.0",
                    onLearnMoreClick = {
                        /* Handle Learn More Click */
                        scope.launch { drawerState.close() }
                    },
                    onExtendLicenseClick = {
                        /* Handle Extend License Click */
                        scope.launch { drawerState.close() }
                    },
                    onLogoutClick = {
                        scope.launch {
                            drawerState.close()
                            onLogout()
                        }
                    },
                )
            }
        },
        content = content
    )
}

@Composable
fun AppDrawerContent(
    companyLogo: Painter? = null,
    companyName: String,
    username: String,
    licenseType: String,
    licenseExpiresIn: String,
    appVersion: String,
    onLearnMoreClick: () -> Unit,
    onExtendLicenseClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(LightGray),
    ) {
        // Header Section
        Column(
            modifier = Modifier
                .background(DeepNavy)
                .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = Color.Gray,
                        shape = CircleShape
                    )
                    .border(2.dp, White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (companyLogo != null) {
                    Icon(
                        painter = companyLogo,
                        contentDescription = "Company Logo",
                        tint = DeepNavy,
                        modifier = Modifier.size(64.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Logo",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = companyName, style = MaterialTheme.typography.titleLarge, color = White)
            Text(text = username, style = MaterialTheme.typography.bodyMedium, color = White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // License Info
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DoubleLineDrawerItem(
                        icon = Icons.Default.Info,
                        primaryText = "License: $licenseType",
                        secondaryText = "Learn more",
                        onClick = onLearnMoreClick
                    )

                    DoubleLineDrawerItem(
                        icon = Icons.Default.Repeat,
                        primaryText = "Extend license",
                        secondaryText = "Expires in $licenseExpiresIn",
                        onClick = onExtendLicenseClick
                    )

                    DoubleLineDrawerItem(
                        icon = Icons.Default.Upgrade,
                        primaryText = "Upgrade license",
                        secondaryText = "Get more features",
                        onClick = onLearnMoreClick
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                DoubleLineDrawerItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    primaryText = "Logout",
                    secondaryText = "Sign out of your account",
                    onClick = onLogoutClick,
                    contentColor = MassecRed
                )
            }

            Text(
                text = "Version $appVersion",
                style = MaterialTheme.typography.labelSmall,
                color = DarkText,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun DoubleLineDrawerItem(
    icon: ImageVector,
    primaryText: String,
    secondaryText: String,
    onClick: () -> Unit,
    contentColor: Color = DeepNavy
) {
    NavigationDrawerItem(
        label = {
            Column {
                Text(
                    text = primaryText,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor
                )
                Text(
                    text = secondaryText,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkText
                )
            }
        },
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = DeepNavy.copy(alpha = 0.1f),
            unselectedTextColor = contentColor,
            selectedTextColor = contentColor
        ),
        selected = false,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = primaryText,
                tint = contentColor
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AppDrawerContentPreview() {
    AppDrawerContent(
        companyName = "Example Company",
        username = "John Doe",
        licenseType = "Premium",
        licenseExpiresIn = "30 days",
        appVersion = "v1.0.0",
        onLearnMoreClick = {},
        onExtendLicenseClick = {},
        onLogoutClick = {}
    )
}