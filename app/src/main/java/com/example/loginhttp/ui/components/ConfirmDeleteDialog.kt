package com.example.loginhttp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.loginhttp.ui.theme.MassecRed

@Composable
fun ConfirmDeleteDialog(
    itemCount: Int,
    title: String = "Potvrda brisanja",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val message = if (itemCount == 1) {
        "Jeste li sigurni da želite obrisati stavku?"
    } else {
        "Jeste li sigurni da želite obrisati $itemCount stavke?"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Da", color = MassecRed)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Ne", color = MassecRed)
            }
        }
    )
}