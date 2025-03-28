package com.example.loginhttp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.loginhttp.ui.theme.DarkGray
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.White

@Composable
fun SelectionToolbar(
    selectedCount: Int,
    onSelectAll: () -> Unit,
    actions: List<Pair<ImageVector, () -> Unit>> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepNavy)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 0.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.padding(top = 0.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Odabrano: $selectedCount",
                    color = White,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(16.dp))

                TextButton(
                    onClick = onSelectAll,
                    colors = ButtonColors(
                        containerColor = White,
                        contentColor = DarkText,
                        disabledContainerColor = DarkGray,
                        disabledContentColor = White
                    ),
                ) {
                    Text("Odaberi sve", color = DarkText)
                }
            }

            Row(
                modifier = Modifier.padding(top = 0.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions.forEach { (icon, action) ->
                    IconButton(
                        onClick = action
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = White
                        )
                    }
                }
            }
        }
    }
}