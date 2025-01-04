package com.example.pokedex.mainViews.SearchView

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    private val pokemonRepository = DependencyContainer.pokemonRepository

    var searchText = mutableStateOf("")

    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList: StateFlow<List<Pokemon>> = _pokemonList.asStateFlow()

    init {
        viewModelScope.launch {
            pokemonRepository.pokemonsFlow.collect { newPokemonList ->
                _pokemonList.value = newPokemonList
            }
        }
        fetchPokemonList()
    }

    fun fetchPokemonList() {
        viewModelScope.launch {
            pokemonRepository.fetchPokemons()
        }
    }

    fun searchPokemonList() {
        viewModelScope.launch {
            pokemonRepository.searchPokemonByName(searchText.value)
        }
    }
}
