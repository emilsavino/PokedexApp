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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.pokedex.shared.PokemonTypeResources


@Composable
fun ProfileButton(
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

@Composable
fun SignedInContent(
    email: String,
    profilePictureUrl: String?,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = profilePictureUrl,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = email, color = Color.Black)

        Spacer(modifier = Modifier.height(8.dp))
        ProfileButton(text = "Sign Out", onClick = onSignOut)

        Spacer(modifier = Modifier.height(8.dp))
        ProfileButton(text = "Delete Account", onClick = onDeleteAccount)
    }
}


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
            email = viewModel.email.value,
            profilePictureUrl = viewModel.profilePictureUrl.value,
            onSignOut = {
                viewModel.signOut(navController)
            },
            onDeleteAccount = {
                viewModel.deleteAccount(navController)
            }
        )

    }
}


@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    val navController = rememberNavController()
    ProfileView(navController = navController)
}
