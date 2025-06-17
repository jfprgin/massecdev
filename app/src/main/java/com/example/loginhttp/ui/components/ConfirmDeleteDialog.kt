package com.example.loginhttp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.loginhttp.R
import com.example.loginhttp.ui.theme.MassecRed

@Composable
fun ConfirmDeleteDialog(
    itemCount: Int,
    title: String = stringResource(R.string.delete_confirmation),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val message = pluralStringResource(R.plurals.confirm_delete_items, itemCount, itemCount)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(R.string.yes), color = MassecRed)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.no), color = MassecRed)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ConfirmDeleteDialogPreview() {
    ConfirmDeleteDialog(
        itemCount = 3,
        onConfirm = {},
        onDismiss = {}
    )
}