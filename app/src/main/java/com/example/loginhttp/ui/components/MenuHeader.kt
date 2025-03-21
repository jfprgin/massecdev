package com.example.loginhttp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.White

@Composable
fun MenuHeader(screenWidth: Dp, title : String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepNavy)
            .padding(horizontal = 16.dp, vertical = (screenWidth * 0.05f)), // proportional padding
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                fontSize = (screenWidth.value * 0.07f).sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
        }
    }
}