package com.example.pokedex.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OptionButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .padding(5.dp)
            .height(60.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(disabledContainerColor = Color.Gray, containerColor = Color.Gray)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold)
    }
}