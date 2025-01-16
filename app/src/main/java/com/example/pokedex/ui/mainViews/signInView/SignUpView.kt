package com.example.pokedex.mainViews.signInView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pokedex.R
import com.example.pokedex.ui.navigation.Screen
import com.example.pokedex.dataClasses.PokemonTypeResources

@Composable
fun SignUpView(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val viewModel = viewModel<SignInViewModel>()
    val gradientColor = PokemonTypeResources().getTypeGradient("dark")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.padding(8.dp))

            Image(
                painter = painterResource(id = R.drawable.pokedex_logo),
                contentDescription = "Pokédex Icon",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "Catch 'em all after you sign in!",
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.padding(8.dp))

            MakeEmailField(viewModel)

            Spacer(modifier = Modifier.padding(4.dp))

            MakePasswordField(viewModel)

            Spacer(modifier = Modifier.padding(12.dp))

            MakeSignUpWithEmailButton(viewModel, navController)

            Spacer(modifier = Modifier.padding(20.dp))

            MakeSignInWithGoogleButton(viewModel, navController)

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = "Already have an account?",
                color = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable {
                        navController.navigate(Screen.SignIn.route)
                    },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
private fun MakeEmailField(viewModel: SignInViewModel) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .shadow(6.dp, CircleShape)
            .background(Color.White),


        placeholder = {
            Text(
                "E-mail",
                color = Color.Black
            )
        },

        onValueChange = {
            viewModel.email.value = it
        },

        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            autoCorrectEnabled = false
        ),

        keyboardActions = KeyboardActions.Default,
        value = viewModel.email.value,
        shape = CircleShape,
        singleLine = true,
    )
}

@Composable
private fun MakePasswordField(viewModel: SignInViewModel) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .shadow(6.dp, CircleShape)
            .background(Color.White),

        placeholder = {
            Text(
                "Password",
                color = Color.Black
            )
        },

        onValueChange = {
            viewModel.password.value = it
        },

        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            autoCorrectEnabled = false
        ),

        keyboardActions = KeyboardActions.Default,
        value = viewModel.password.value,
        shape = CircleShape,
        singleLine = true,

        visualTransformation = PasswordVisualTransformation()
    )
}

@Composable
private fun MakeSignUpWithEmailButton(viewModel: SignInViewModel, navController: NavController) {
    OutlinedButton(
        onClick = {
            viewModel.signUpWithEmail(
                viewModel.email.value,
                viewModel.password.value,
                navController
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(6.dp, CircleShape),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        shape = CircleShape,
    ) {
        Text(
            text = "Sign-up using email",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun MakeSignInWithGoogleButton(viewModel: SignInViewModel, navController: NavController) {
    OutlinedButton(
        onClick = {
            viewModel.signInWithGoogle(navController)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(6.dp, CircleShape),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        shape = CircleShape
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = "Sign-up with Google",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}