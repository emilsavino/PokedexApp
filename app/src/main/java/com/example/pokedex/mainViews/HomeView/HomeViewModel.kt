package com.example.pokedex.mainViews.HomeView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val pokemonRepository = PokemonRepository

    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList: StateFlow<List<Pokemon>> = _pokemonList

    init {
        viewModelScope.launch {
            pokemonRepository.pokemonsFlow
                .collect { pokemons ->
                    _pokemonList.update {
                        pokemons
                    }
                }
        }
        fetchPokemons()
    }


    private fun fetchPokemons() = viewModelScope.launch {
        pokemonRepository.fetchPokemons()
    }
}