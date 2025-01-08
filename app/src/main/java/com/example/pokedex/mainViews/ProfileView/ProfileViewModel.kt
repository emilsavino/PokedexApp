package com.example.pokedex.mainViews.ProfileView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _userEmail = MutableStateFlow("name@dtu.dk")
    val userEmail: StateFlow<String> = _userEmail

    fun updateEmail(newEmail: String) {
        viewModelScope.launch {
            _userEmail.value = newEmail
        }
    }


    fun signOut() {
        // TODO: Add functionality to sign out
    }

    fun fetchUserData(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            updateEmail(currentUser.email ?: "No email found")
        }
        return userEmail.value

    }
}