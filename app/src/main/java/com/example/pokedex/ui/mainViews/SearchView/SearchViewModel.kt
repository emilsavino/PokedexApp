package com.example.pokedex.mainViews.SearchView

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.dataClasses.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    private val recentlySearchedRepository = DependencyContainer.recentlySearchedRepository
    private var lastSentRequest : Int = 0
    val connectivityRepository = DependencyContainer.connectivityRepository

    var selectedFilterOptionsList = mutableStateOf<List<String>>(emptyList())
    var selectedSortOption = mutableStateOf("")
    var searchText = mutableStateOf("")

    private val _pokemonList: MutableStateFlow<SearchUIState> = MutableStateFlow(SearchUIState.Empty)
    val pokemonList: StateFlow<SearchUIState> = _pokemonList.asStateFlow()

    init {
        viewModelScope.launch {
            recentlySearchedRepository.searchFlow.collect { newPokemonList ->
                if (newPokemonList.indexOfSearch == lastSentRequest || (newPokemonList.indexOfSearch == -1))
                {
                    _pokemonList.update {
                        SearchUIState.Data(newPokemonList.pokemons)
                    }
                }
            }
        }
        searchPokemonList()
    }

    fun searchPokemonList() {
        _pokemonList.update {
            SearchUIState.Loading
        }

        if (searchText.value.isEmpty() && selectedFilterOptionsList.value.isEmpty() && selectedSortOption.value.isEmpty())
        {
            viewModelScope.launch {
               recentlySearchedRepository.fetchRecentlySearched(++lastSentRequest)
            }
            return
        }

        viewModelScope.launch {
            recentlySearchedRepository.searchPokemonByNameAndFilterWithSort(searchText.value,0, selectedFilterOptionsList.value, selectedSortOption.value,++lastSentRequest)
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
        return recentlySearchedRepository.filterOptions
    }

    fun getAllSortOptions() : List<String>
    {
        return recentlySearchedRepository.sortOptions
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

    fun addPokemonToRecentlySearched(name : String)
    {
        viewModelScope.launch {
            recentlySearchedRepository.addToRecentlySearched(name)
        }
    }
}

sealed class SearchUIState {
    data class Data(val pokemonList: List<Pokemon>) : SearchUIState()
    object Loading : SearchUIState()
    object Empty : SearchUIState()
}
