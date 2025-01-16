package com.example.pokedex.mainViews.splashScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.ui.navigation.Screen
import kotlinx.coroutines.launch

class SplashScreenViewModel : ViewModel(){
    private val authManager = DependencyContainer.googleAuthenticationManager
    private val connectivityRepository = DependencyContainer.connectivityRepository
    private val dataStore = DependencyContainer.pokemonDataStore

    private var isSignedIn = authManager.fetchSignedIn()

    fun onAppear (navController: NavController) {
        if (connectivityRepository.isConnected.asLiveData().value == false && dataStore.getAllPokemonResults().size < 1000) {
            navController.navigate(Screen.Home.route)
            return
        }

        viewModelScope.launch{
            if(isSignedIn){
                navController.navigate(Screen.Home.route)
            } else {
                navController.navigate(Screen.SignIn.route)
            }
        }
    }
}