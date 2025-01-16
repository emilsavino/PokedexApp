package com.example.pokedex.mainViews.signInView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.manager.AuthResponse
import com.example.pokedex.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    private val connectivityRepository = DependencyContainer.connectivityRepository
    var email = mutableStateOf("")
    var profilePictureUrl = mutableStateOf<String?>(null)
    var authError = mutableStateOf<String?>(null)
    var failedToSignIn by mutableStateOf(false)
    var password = mutableStateOf("")

    val googleAuthManager = DependencyContainer.googleAuthenticationManager
    val emailAuthManager = DependencyContainer.emailAuthManager


    fun signInWithGoogle(navController: NavController) {
        if (connectivityRepository.isConnected.asLiveData().value == false) {
            failedToSignIn = true
            return
        }
        viewModelScope.launch {
            googleAuthManager.signInWithGoogle().collectLatest { response ->
                when (response) {
                    is AuthResponse.Success -> {
                        val currentUser = googleAuthManager.auth.currentUser
                        email.value = currentUser?.email ?: "Unknown User"
                        profilePictureUrl.value = currentUser?.photoUrl?.toString()
                        authError.value = null
                        navController.navigate(Screen.Home.route)
                        failedToSignIn = false
                    }

                    is AuthResponse.Error -> {
                        authError.value = response.message
                    }
                }
            }
        }
    }

    fun signInWithEmail(
        enteredEmail: String,
        enteredPassword: String,
        navController: NavController
    ) {
        if (connectivityRepository.isConnected.asLiveData().value == false) {
            failedToSignIn = true
            return
        }
        viewModelScope.launch {
            emailAuthManager.loginWithEmail(enteredEmail, enteredPassword)
                .collectLatest { response ->
                    when (response) {
                        is AuthResponse.Success -> {
                            email.value = enteredEmail
                            profilePictureUrl.value = null
                            authError.value = null
                            navController.navigate(Screen.Home.route)
                        }

                        is AuthResponse.Error -> {
                            authError.value = response.message
                        }
                    }
                }
        }
    }

    fun signUpWithEmail(
        enteredEmail: String,
        enteredPassword: String,
        navController: NavController
    ) {
        viewModelScope.launch {
            emailAuthManager.createAccountWithEmail(enteredEmail, enteredPassword)
                .collectLatest { response ->
                    when (response) {
                        is AuthResponse.Success -> {
                            email.value = enteredEmail
                            profilePictureUrl.value = null
                            authError.value = null
                            navController.navigate(Screen.Home.route)
                        }

                        is AuthResponse.Error -> {
                            authError.value = response.message
                        }
                    }
                }

        }
    }
}