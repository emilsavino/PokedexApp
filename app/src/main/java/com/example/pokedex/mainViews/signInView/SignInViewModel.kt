package com.example.pokedex.mainViews.signInView

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.manager.AuthResponse
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    var isSignedIn = mutableStateOf(false)
    var email = mutableStateOf("Guest")
    var profilePictureUrl = mutableStateOf<String?>(null)
    var authError = mutableStateOf<String?>(null)

    val googleAuthManager = DependencyContainer.googleAuthenticationManager


    fun signInWithGoogle() {
        viewModelScope.launch {
            googleAuthManager.signInWithGoogle().collectLatest { response ->
                when (response) {
                    is AuthResponse.Success -> {
                        val currentUser = googleAuthManager.auth.currentUser
                        isSignedIn.value = true
                        email.value = currentUser?.email ?: "Unknown User"
                        profilePictureUrl.value = currentUser?.photoUrl?.toString()
                        authError.value = null
                    }
                    is AuthResponse.Error -> {
                        authError.value = response.message
                    }
                }
            }
        }
    }
}