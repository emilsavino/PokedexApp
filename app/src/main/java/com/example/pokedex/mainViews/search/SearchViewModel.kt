package com.example.pokedex.mainViews.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    private val pokemonRepository = PokemonRepository()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _pokemons = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemons: StateFlow<List<Pokemon>> = _pokemons.asStateFlow()

    init {
        fetchMorePokemons()
    }

    fun fetchMorePokemons() {
        viewModelScope.launch {
            pokemonRepository.fetchPokemons()
            pokemonRepository.pokemonsFlow.collect { newPokemonList ->
                _pokemons.value = _pokemons.value + newPokemonList
            }
        }
    }

    fun updateSearchText(newText: String) {
        _searchText.value = newText
    }
}
