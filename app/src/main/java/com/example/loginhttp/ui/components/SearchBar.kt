package com.example.loginhttp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginhttp.ui.theme.DarkGray
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String = "Search",
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholderText,
                    color = DarkGray,
                    fontSize = 18.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = DeepNavy
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = DeepNavy,
                focusedIndicatorColor = MassecRed,
                focusedContainerColor = LightGray,
                unfocusedContainerColor = LightGray
            ),
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp)
        )
    }

}

