package com.example.loginhttp.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.loginhttp.LoginViewModel
import com.example.loginhttp.R
import com.example.loginhttp.ui.theme.DarkGray
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.LoginHTTPTheme
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.White
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var loginResult by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }

    // Collect login result state
    LaunchedEffect(Unit) {
        viewModel.getSavedCredentials { savedUsername, savedPassword ->
            username = savedUsername
            password = savedPassword
            rememberMe = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 32.dp, horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center), // Align column in the center of the Box
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Image
            Image(
                painter = painterResource(id = R.drawable.logo_massec),
                contentScale = ContentScale.FillBounds,
                contentDescription = "Massec Logo",
                modifier = Modifier.height(80.dp).fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Username TextField
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                leadingIcon = {
                    Icon(Icons.Rounded.AccountCircle,
                        contentDescription = "Username Icon",
                        tint = DeepNavy
                    )
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password TextField
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(Icons.Rounded.Lock,
                        contentDescription = "Password Icon",
                        tint = DeepNavy
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                trailingIcon = {
                    if (passwordVisible)
                        Icon(Icons.Rounded.Visibility,
                            contentDescription = "Hide Password",
                            tint = DeepNavy,
                            modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                        )
                    else
                        Icon(Icons.Rounded.VisibilityOff,
                            contentDescription = "Show Password",
                            tint = DeepNavy,
                            modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                        )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxColors(
                        checkedCheckmarkColor = White,
                        uncheckedCheckmarkColor = White,
                        checkedBoxColor = DeepNavy,
                        uncheckedBoxColor = White,
                        disabledCheckedBoxColor = LightGray,
                        disabledUncheckedBoxColor = LightGray,
                        disabledIndeterminateBoxColor = LightGray,
                        checkedBorderColor = DeepNavy,
                        uncheckedBorderColor = DeepNavy,
                        disabledBorderColor = DarkGray,
                        disabledUncheckedBorderColor = DarkGray,
                        disabledIndeterminateBorderColor = DarkGray,
                    )
                )
                Text(
                    text = "Remember me",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp),
                    color = DarkGray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Login with Credentials Button
            Button(
                onClick = { viewModel.loginWithCredentials(username, password, rememberMe) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy)
            ) {
                Text(
                    text = "Login with credentials",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login with Device clickable text (Centered)
            Text(
                text = "Login with device",
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { viewModel.loginWithDevice("MASSEC 1234", "00:06:11:22:22") }
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = DeepNavy,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        LaunchedEffect(viewModel.loginState) {
            viewModel.loginState?.collect { result ->
                loginResult = result
            }
        }

        // Login result in fixed position at the bottom of the screen
        if (loginResult.isNotEmpty()) {
            Text(
                text = loginResult,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.BottomCenter) // Correct use of alignment on Box
                    .padding(16.dp) // Provide space from the bottom
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginHTTPTheme {
        LoginScreen()
    }
}