package com.example.pokedex.mainViews.ProfileView

import ProfileViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.pokedex.R


@Composable
fun ProfileButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color = Color.LightGray,
    contentColor: Color = Color.Black
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = text)
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
        horizontalAlignment = Alignment.CenterHorizontally
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
        Text(text = email)

        Spacer(modifier = Modifier.height(8.dp))
        ProfileButton(text = "Sign Out", onClick = onSignOut, contentColor = Color.Red)

        Spacer(modifier = Modifier.height(8.dp))
        ProfileButton(text = "Delete Account", onClick = onDeleteAccount, contentColor = Color.Red)
    }
}

@Composable
fun SignedOutContent(
    onSignIn: () -> Unit,
    authError: String?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("You are not signed in.")

        Spacer(modifier = Modifier.height(16.dp))
        ProfileButton(text = "Sign In with Google", onClick = onSignIn)

        authError?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProfileView(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val isSignedIn by viewModel.isSignedIn.collectAsState()
    val email by viewModel.email.collectAsState()
    val profilePictureUrl by viewModel.profilePictureUrl.collectAsState()
    val authError by viewModel.authError.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFDD99)),
        contentAlignment = Alignment.Center
    ) {
        if (isSignedIn) {
            SignedInContent(
                email = email,
                profilePictureUrl = profilePictureUrl,
                onSignOut = { viewModel.signOut() },
                onDeleteAccount = { viewModel.deleteAccount() }
            )
        } else {
            SignedOutContent(
                onSignIn = { viewModel.signInWithGoogle() },
                authError = authError
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    val navController = rememberNavController()
    ProfileView(navController = navController)
}
