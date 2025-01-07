package com.example.pokedex.mainViews.SearchView

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    private val pokemonRepository = DependencyContainer.pokemonRepository
    private val selectedOptionsList = MutableStateFlow<List<String>>(mutableListOf())
    val selectedOptionsFlow: StateFlow<List<String>> = selectedOptionsList.asStateFlow()
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
            pokemonRepository.searchPokemonByName(searchText.value,0)
        }
    }

    fun selectOption(option : String)
    {
        if (selectedOptionsList.value.contains(option))
        {
            selectedOptionsList.value = selectedOptionsList.value.toMutableList().apply {
                remove(option)
            }
        }
        else
        {
            selectedOptionsList.value = selectedOptionsList.value.toMutableList().apply {
                add(option)
            }
        }
    }

    fun getAllFilterOptions() : List<String>
    {
        return pokemonRepository.filterOptions
    }
}
