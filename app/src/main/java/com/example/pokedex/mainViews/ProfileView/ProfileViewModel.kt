package com.example.pokedex.mainViews.ProfileView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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