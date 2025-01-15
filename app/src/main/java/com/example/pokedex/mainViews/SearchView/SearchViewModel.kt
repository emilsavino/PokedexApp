package com.example.pokedex.mainViews.SearchView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    private val recentlySearchedRepository = DependencyContainer.recentlySearchedRepository
    private val connectivityRepository = DependencyContainer.connectivityRepository
    var hasInternet by mutableStateOf(connectivityRepository.isConnected.asLiveData())

    var selectedFilterOptionsList = mutableStateOf<List<String>>(emptyList())
    var selectedSortOption = mutableStateOf("")
    var searchText = mutableStateOf("")

    private val _pokemonList: MutableStateFlow<SearchUIState> = MutableStateFlow(SearchUIState.Empty)
    val pokemonList: StateFlow<SearchUIState> = _pokemonList.asStateFlow()

    init {
        viewModelScope.launch {
            recentlySearchedRepository.searchFlow.collect { newPokemonList ->
                _pokemonList.update {
                    SearchUIState.Data(newPokemonList)
                }
            }
        }
        searchPokemonList()
    }

    fun searchPokemonList() {
        if (!hasInternet.value!!) {
            _pokemonList.update {
                SearchUIState.NoInternet
            }
            return
        }

        _pokemonList.update {
            SearchUIState.Loading
        }

        if (searchText.value.isEmpty() && selectedFilterOptionsList.value.isEmpty() && selectedSortOption.value.isEmpty())
        {
            viewModelScope.launch {
               recentlySearchedRepository.fetchRecentlySearched()
            }
            return
        }

        viewModelScope.launch {
            recentlySearchedRepository.searchPokemonByNameAndFilterWithSort(searchText.value,0, selectedFilterOptionsList.value, selectedSortOption.value)
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
    object NoInternet : SearchUIState()
}
