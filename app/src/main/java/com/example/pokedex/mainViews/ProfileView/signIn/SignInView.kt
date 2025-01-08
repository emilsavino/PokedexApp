package com.example.pokedex.mainViews.ProfileView.signIn

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pokedex.R
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun SignInView(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val viewModel = viewModel<SignInViewModel>()
    val signInState = viewModel.signInState.collectAsState().value
    val context = LocalContext.current
    val authenticationManager = remember {
        GoogleAuthenticationManager(context)
    }
    val coroutineScope = rememberCoroutineScope()

    when(signInState) {
        is SignInState.Success -> {
            navController.navigate("ProfileView") {
                popUpTo("SignInView") { inclusive = true }
            }
        }
        is SignInState.Error -> {
            Text(text = signInState.message)
        }
        else -> Unit
        }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Please sign in to connect",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        OutlinedButton(
            onClick = {
                authenticationManager.signInWithGoogle()
                    .onEach { response ->
                        if (response is AuthResponse.Success) {
                            viewModel.updateSignInStatus(SignInState.Success)
                        } else if (response is AuthResponse.Error) {
                            viewModel.updateSignInStatus(SignInState.Error(response.message))
                        }
                    }
                    .launchIn(coroutineScope)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                // TODO: Replace charmander with google logo

                painter = painterResource(id = R.drawable.charmander),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Sign-in with Google",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}