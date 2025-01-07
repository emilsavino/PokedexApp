package com.example.pokedex.mainViews.HomeView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.repositories.PokemonOfTheDayRepository
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val recentlyViewedRepository = DependencyContainer.recentlyViewedRepository
    private val pokemonOfTheDayRepository = DependencyContainer.pokemonOfTheDayRepository

    private val _pokemonOfTheDay = MutableStateFlow<HomeUIState>(HomeUIState.Empty)
    val pokemonOfTheDay: StateFlow<HomeUIState> = _pokemonOfTheDay.asStateFlow()

    private val _recentlyViewedPokemons = MutableStateFlow<RecentsUIState>(RecentsUIState.Empty)
    val recentlyViewedPokemons: StateFlow<RecentsUIState> = _recentlyViewedPokemons.asStateFlow()

    init {
        viewModelScope.launch {
            recentlyViewedRepository.recentlyViewedPokemonsFlow
                .collect { pokemons ->
                    _recentlyViewedPokemons.update {
                        RecentsUIState.Data(pokemons)
                    }
                }
        }

        viewModelScope.launch {
            pokemonOfTheDayRepository.pokemonOfTheDayFlow
                .collect { pokemon ->
                    _pokemonOfTheDay.update {
                        HomeUIState.Data(pokemon)
                    }
                }
        }

        getRecentlyViewedPokemons()
        getPokemonOfTheDay()
    }

    fun getPokemonOfTheDay() = viewModelScope.launch {
        _pokemonOfTheDay.update {
            HomeUIState.Loading
        }
        pokemonOfTheDayRepository.getPokemonOfTheDayByName("pikachu")
    }

    fun getRecentlyViewedPokemons() = viewModelScope.launch {
        _recentlyViewedPokemons.update {
            RecentsUIState.Loading
        }
        recentlyViewedRepository.fetchRecents()
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