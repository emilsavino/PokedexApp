package com.example.pokedex.mainViews.splashScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.ui.navigation.Screen
import kotlinx.coroutines.launch

class SplashScreenViewModel(val navController: NavController) : ViewModel() {
    private val pokemonDataStore = DependencyContainer.pokemonDataStore
    private val authManager = DependencyContainer.googleAuthenticationManager
    private val connectivityRepository = DependencyContainer.connectivityRepository
    private val dataStore = DependencyContainer.pokemonDataStore

    private var isSignedIn = authManager.fetchSignedIn()

    init {
        viewModelScope.launch {
            pokemonDataStore.pokemonMapSizeFlow.collect { size ->
                if (size > 500)
                {
                    navController.navigate(Screen.Home.route)
                }
            }
        }
    }

    fun onAppear(navController: NavController) {
        viewModelScope.launch {
            if (!isSignedIn) {
                navController.navigate(Screen.SignIn.route)
            }
        }
    }
}