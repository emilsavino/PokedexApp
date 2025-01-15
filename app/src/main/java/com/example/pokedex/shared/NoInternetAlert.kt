package com.example.pokedex.shared

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NoInternetAlert(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss()},
        title = { Text("No Internet Connection") },
        text = { Text("You need connection to the internet in order to access this content") },
        confirmButton = {
            Button(
                onClick = { onDismiss() },
            ) {
                Text("Okay")
            }
        }
    )
}