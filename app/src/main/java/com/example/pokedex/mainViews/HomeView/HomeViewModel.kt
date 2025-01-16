package com.example.pokedex.mainViews.HomeView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.navigation.Screen
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val recentlyViewedRepository = DependencyContainer.recentlyViewedRepository
    private val pokemonOfTheDayRepository = DependencyContainer.pokemonOfTheDayRepository
    private val connectivityRepository = DependencyContainer.connectivityRepository
    var showNoInternetAlert by mutableStateOf(false)


    private val _pokemonOfTheDay = MutableStateFlow<HomeUIState>(HomeUIState.Empty)
    val pokemonOfTheDay: StateFlow<HomeUIState> = _pokemonOfTheDay.asStateFlow()

    private val _recentlyViewedPokemons = MutableStateFlow<RecentsUIState>(RecentsUIState.Empty)
    val recentlyViewedPokemons: StateFlow<RecentsUIState> = _recentlyViewedPokemons.asStateFlow()

    init {
        viewModelScope.launch {
            recentlyViewedRepository.recentlyViewedPokemonsFlow
                .collect { pokemons ->
                    _recentlyViewedPokemons.update {
                        if (pokemons.isEmpty()) {
                            RecentsUIState.Empty
                        } else {
                            RecentsUIState.Data(pokemons)
                        }
                    }
                }
        }

        viewModelScope.launch {
            pokemonOfTheDayRepository.pokemonOfTheDayFlow
                .collect { pokemon ->
                    if (pokemon == null) {
                        _pokemonOfTheDay.update {
                            HomeUIState.Empty
                        }
                    } else {
                        _pokemonOfTheDay.update {
                            HomeUIState.Data(pokemon)
                        }
                    }
                }
        }
    }

    fun getPokemonOfTheDay() = viewModelScope.launch {
        _pokemonOfTheDay.update {
            HomeUIState.Loading
        }
        pokemonOfTheDayRepository.getPokemonOfTheDayByName()
    }

    fun getRecentlyViewedPokemons() = viewModelScope.launch {
        _recentlyViewedPokemons.update {
            RecentsUIState.Loading
        }
        recentlyViewedRepository.fetchRecents()
    }

    fun onWhoIsThatPokemonClicked(navController: NavController) {
        if (connectivityRepository.isConnected.asLiveData().value == true) {
            navController.navigate(Screen.WhoIsThatPokemon.route)
        } else {
            showNoInternetAlert = true
        }
    }
}

sealed class HomeUIState {
    data class Data(val pokemonOfTheDay: Pokemon) : HomeUIState()
    object Loading : HomeUIState()
    object Empty : HomeUIState()
}

sealed class RecentsUIState {
    data class Data(val pokemons: List<Pokemon>) : RecentsUIState()
    object Loading : RecentsUIState()
    object Empty : RecentsUIState()
}