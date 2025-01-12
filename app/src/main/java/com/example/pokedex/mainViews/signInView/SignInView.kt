package com.example.pokedex.mainViews.signInView


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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pokedex.R
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.dependencyContainer.DependencyContainer.init
import com.example.pokedex.manager.AuthResponse
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun SignInView(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val viewModel = viewModel<SignInViewModel>()

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
                viewModel.signInWithGoogle(navController)
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