package com.example.pokedex.mainViews.splashScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.navigation.Screen
import kotlinx.coroutines.launch

class SplashScreenViewModel : ViewModel(){
    private val authManager = DependencyContainer.googleAuthenticationManager

    fun onAppear (navController: NavController) {
        viewModelScope.launch{
            var isSignedIn = authManager.fetchSignedIn()

            if(isSignedIn){
                navController.navigate(Screen.Home.route)
            } else {
                navController.navigate(Screen.SignIn.route)
            }
        }
    }
}