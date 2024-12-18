package com.example.pokedex.mainViews.HomeView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.MockPokemonDataStore
import com.example.pokedex.shared.MockPokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _pokemonList = MutableStateFlow<List<MockPokemon>>(emptyList())
    val pokemonList: StateFlow<List<MockPokemon>> = _pokemonList

    init {
        fetchPokemons()
    }

    private fun fetchPokemons() {
        viewModelScope.launch {
            _pokemonList.value = MockPokemonDataStore().fetchPokemons()
        }
    }
}