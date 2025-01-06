package com.example.pokedex.mainViews.HomeView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val pokemonRepository = DependencyContainer.pokemonRepository

    private val _pokemonOfTheDay = MutableStateFlow<HomeUIState>(HomeUIState.Empty)
    val pokemonOfTheDay: StateFlow<HomeUIState> = _pokemonOfTheDay.asStateFlow()

    init {
        viewModelScope.launch {
            pokemonRepository.pokemonFlow
                .collect { pokemon ->
                    _pokemonOfTheDay.update {
                        HomeUIState.Data(pokemon)
                    }
                }
        }
        getPokemonOfTheDay()
    }

    private fun getPokemonOfTheDay() = viewModelScope.launch {
        _pokemonOfTheDay.update {
            HomeUIState.Loading
        }
        pokemonRepository.getPokemonByName("pikachu")
    }
}

sealed class HomeUIState {
    data class Data(val pokemonOfTheDay: Pokemon) : HomeUIState()
    object Loading : HomeUIState()
    object Empty : HomeUIState()
}