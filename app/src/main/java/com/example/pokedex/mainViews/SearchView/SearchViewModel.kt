package com.example.pokedex.mainViews.SearchView

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    private val pokemonRepository = DependencyContainer.pokemonRepository

    var searchText = mutableStateOf("")

    private val _pokemonList = MutableStateFlow<List<Result>>(emptyList())
    val pokemonList: StateFlow<List<Result>> = _pokemonList.asStateFlow()

    init {
        viewModelScope.launch {
            pokemonRepository.searchFlow.collect { newPokemonList ->
                _pokemonList.value = newPokemonList
            }
        }
        searchPokemonList()
    }

    fun searchPokemonList() {
        viewModelScope.launch {
            pokemonRepository.searchPokemonByName(searchText.value,19)
        }
    }
}
