package com.example.pokedex.mainViews

import android.view.Display.Mode
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProfileButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color = Color.Gray,
    contentColor: Color = Color.Black
){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Gray,
            contentColor = Color.Black
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = text)
    }

}

@Composable
fun ProfileView(modifier: Modifier = Modifier) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            Text("Profile Picture")

            Spacer(modifier = Modifier.height(16.dp))

            Text("name@dtu.dk")

            Spacer(modifier = Modifier.height(16.dp))

            ProfileButton(
                text = "Change Password",
                onClick = { /* Add button functionality */ },
                containerColor = Color.Gray,
                contentColor = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProfileButton(
                text = "Sign Out",
                onClick = { /* Add button functionality */ },
                containerColor = Color.Gray,
                contentColor = Color.Red
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProfileButton(
                text = "Delete Account",
                onClick = { /* Add button functionality */ },
                containerColor = Color.Gray,
                contentColor = Color.Red
            )

            Spacer(modifier = Modifier.height(8.dp))

        }
    }

}

@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    ProfileView()
}
