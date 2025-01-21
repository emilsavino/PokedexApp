package com.example.pokedex.mainViews.SearchView

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.dataClasses.Pokemon
import com.example.pokedex.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class SearchViewModel: ViewModel() {
    open val suggestionText: String = "Recents"
    protected val recentlySearchedRepository = DependencyContainer.recentlySearchedRepository
    protected val teamsRepository = DependencyContainer.teamsRepository
    protected var lastSentRequest : Int = 0
    val connectivityRepository = DependencyContainer.connectivityRepository
    var searchOffset = 0
    var selectedFilterOptionsList = mutableStateOf<List<String>>(emptyList())
    var selectedSortOption = mutableStateOf("")
    var searchText = mutableStateOf("")

    protected val _pokemonList: MutableStateFlow<SearchUIState> = MutableStateFlow(SearchUIState.Empty)
    val pokemonList: StateFlow<SearchUIState> = _pokemonList.asStateFlow()

    init {
        viewModelScope.launch {
            recentlySearchedRepository.searchFlow.collect { newPokemonList ->
                if (newPokemonList.indexOfSearch == lastSentRequest || (newPokemonList.indexOfSearch == -1))
                {
                    if (newPokemonList.pokemons.isEmpty())
                    {
                        if (searchText.value == "")
                        {
                            viewModelScope.launch {
                                recentlySearchedRepository.searchPokemonByNameAndFilterWithSort(searchText.value,searchOffset, selectedFilterOptionsList.value, selectedSortOption.value,++lastSentRequest)
                            }
                        }
                        else
                        {
                            _pokemonList.update {
                                SearchUIState.Empty
                            }
                        }
                    }
                    else
                    {
                        _pokemonList.update {
                            SearchUIState.Data(newPokemonList.pokemons)
                        }
                    }
                }
            }
        }
    }

    open fun searchPokemonList() {
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
            recentlySearchedRepository.searchPokemonByNameAndFilterWithSort(searchText.value,searchOffset, selectedFilterOptionsList.value, selectedSortOption.value,++lastSentRequest)
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
        searchOffset = 0
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
        searchOffset = 0
        searchPokemonList()
    }

    fun addPokemonToRecentlySearched(name : String)
    {
        viewModelScope.launch {
            recentlySearchedRepository.addToRecentlySearched(name)
        }
    }

    open fun onPokemonClicked(pokemon: Pokemon, navController: NavController) {
        addPokemonToRecentlySearched(pokemon.name)
        navController.navigate(Screen.PokemonDetails.createRoute(pokemon.name))
    }

    open fun onLongClick(pokemon: Pokemon, navController: NavController) {
        onPokemonClicked(pokemon, navController)
    }
}

sealed class SearchUIState {
    data class Data(val pokemonList: List<Pokemon>) : SearchUIState()
    object Loading : SearchUIState()
    object Empty : SearchUIState()
}
