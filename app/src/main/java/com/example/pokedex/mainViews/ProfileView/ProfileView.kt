package com.example.pokedex.mainViews.ProfileView

import ProfileViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.shared.NoInternetAlert
import com.example.pokedex.shared.PokemonTypeResources


@Composable
fun ProfileView(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val viewModel = viewModel<ProfileViewModel>()
    val gradient = PokemonTypeResources().appGradient()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        SignedInContent(
            viewModel = viewModel,
            navController = navController
        )

    }
}

@Composable
private fun SignedInContent(
    viewModel: ProfileViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = viewModel.profilePictureUrl.value,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = viewModel.email.value, color = Color.Black)

        Spacer(modifier = Modifier.height(8.dp))
        ProfileButton(text = "Sign Out", onClick = { viewModel.signOut(navController) })

        Spacer(modifier = Modifier.height(8.dp))
        ProfileButton(text = "Delete Account", onClick = { viewModel.deleteAccount(navController) })

        if (viewModel.showNoInternetAlert) {
            NoInternetAlert(tryingToDo = "delete your account", onDismiss = { viewModel.showNoInternetAlert = false })
        }
    }
}

@Composable
private fun ProfileButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color = Color.White,
    contentColor: Color = Color.Black
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, CircleShape)
    ) {
        Text(text = text, fontSize = 16.sp)
    }
}