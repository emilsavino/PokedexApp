package com.example.pokedex.mainViews.ProfileView

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pokedex.R

class ProfileViewModel : ViewModel() {
    private val _userEmail = MutableStateFlow("name@dtu.dk")
    val userEmail: StateFlow<String> = _userEmail

    private val _userPassword = MutableStateFlow("********")
    val userPassword: StateFlow<String> = _userPassword

    fun updateEmail(newEmail: String){
        viewModelScope.launch {
            _userEmail.value = newEmail
        }
    }

    fun changePassword(newPassword : String){
        viewModelScope.launch {
            _userPassword.value = newPassword
        }
    }

    fun signOut(){
        // TODO: Add functionality to sign out
    }

    fun deleteAccount(){
        // TODO: Add functionality to delete account
    }

}

@Composable
fun ProfileButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color = Color.LightGray,
    contentColor: Color = Color.Black
){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray,
            contentColor = Color.Black
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = text)
    }

}

@Composable
fun ProfileView(
    profileViewModel: ProfileViewModel = viewModel(),
    modifier: Modifier = Modifier,
    navController: NavController
){

    val email = profileViewModel.userEmail.collectAsState().value
    val password = profileViewModel.userPassword.collectAsState().value

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFDD99)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ){

            Image(
                painter = painterResource(id = R.drawable.charmander),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(email)

            Spacer(modifier = Modifier.height(16.dp))

            Text(password)

            Spacer(modifier = Modifier.height(16.dp))

            ProfileButton(
                text = "Change Password",
                onClick = { /* Add button functionality */ },
                containerColor = Color.LightGray,
                contentColor = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProfileButton(
                text = "Sign Out",
                onClick = { /* Add button functionality */ },
                containerColor = Color.LightGray,
                contentColor = Color.Red
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProfileButton(
                text = "Delete Account",
                onClick = { /* Add button functionality */ },
                containerColor = Color.LightGray,
                contentColor = Color.Red
            )

            Spacer(modifier = Modifier.height(8.dp))

        }
    }

}

@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    val navController = rememberNavController()
    ProfileView(navController = navController)
}
