package com.example.pokedex.mainViews.search

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class SearchViewModel(
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    var searchText by mutableStateOf("")
        private set

    private val _filteredPokemons = MutableStateFlow<List<Pokemon>>(emptyList())
    val filteredPokemons: StateFlow<List<Pokemon>> = _filteredPokemons

    init {
        viewModelScope.launch {
            pokemonRepository.fetchPokemons()
        }
    }

    fun updateSearchText(newText: String) {
        searchText = newText
        filterPokemons(newText)
    }

    private fun filterPokemons(query: String) {
        viewModelScope.launch {
            pokemonRepository.pokemonsFlow
                .filter { it.isNotEmpty() }
                .collect { pokemons ->
                    _filteredPokemons.value = if (query.isBlank()) {
                        pokemons
                    } else {
                        pokemons.filter { it.name.contains(query, ignoreCase = true) }
                    }
                }
        }
    }
}
