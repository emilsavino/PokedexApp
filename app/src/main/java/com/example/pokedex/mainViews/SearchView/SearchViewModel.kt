package com.example.pokedex.mainViews.SearchView

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    private val pokemonRepository = DependencyContainer.pokemonRepository

    var selectedFilterOptionsList = mutableStateOf<List<String>>(emptyList())
    var selectedSortOption = mutableStateOf("")
    var searchText = mutableStateOf("")

    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList: StateFlow<List<Pokemon>> = _pokemonList.asStateFlow()

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
            pokemonRepository.searchPokemonByNameAndFilterWithSort(searchText.value,0, selectedFilterOptionsList.value, selectedSortOption.value)
        }
    }

    fun selectFilterOption(option : String)
    {
        if (selectedFilterOptionsList.value.contains(option))
        {
            selectedFilterOptionsList.value = selectedFilterOptionsList.value.toMutableList().apply {
                remove(option)
            }
        }
        else
        {
            selectedFilterOptionsList.value = selectedFilterOptionsList.value.toMutableList().apply {
                add(option)
            }
        }
        searchPokemonList()
    }

    fun getAllFilterOptions() : List<String>
    {
        return pokemonRepository.filterOptions
    }

    fun getAllSortOptions() : List<String>
    {
        return pokemonRepository.sortOptions
    }

    fun selectSortOption(option : String)
    {
        if (selectedSortOption.value == option)
        {
            selectedSortOption.value = ""
        }
        else
        {
            selectedSortOption.value = option
        }
        searchPokemonList()
    }
}

sealed class SearchUIState {
    data class Data(val pokemonList: List<Pokemon>) : SearchUIState()
    object Loading : SearchUIState()
    object Empty : SearchUIState()
}
