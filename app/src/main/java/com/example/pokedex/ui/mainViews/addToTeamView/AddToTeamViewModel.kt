package com.example.pokedex.ui.mainViews.addToTeamView

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedex.dataClasses.Pokemon
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.mainViews.SearchView.SearchUIState
import com.example.pokedex.mainViews.SearchView.SearchViewModel
import com.example.pokedex.ui.navigation.Screen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddToTeamViewModel(private val teamName: String, private val dismiss: () -> Unit) : SearchViewModel() {
    override fun searchPokemonList() {
        _pokemonList.update {
            SearchUIState.Loading
        }

        if (searchText.value.isEmpty() && selectedFilterOptionsList.value.isEmpty() && selectedSortOption.value.isEmpty() && teamName != null)
        {
            viewModelScope.launch {
                teamsRepository.fetchTeamSuggestions(teamName)
                recentlySearchedRepository.searchFlow.collect { newPokemonList ->
                    if (newPokemonList.indexOfSearch == lastSentRequest || newPokemonList.indexOfSearch == -1) {
                        _pokemonList.update {
                            val shuffledList = newPokemonList.pokemons.shuffled()
                            if (shuffledList.isEmpty()) SearchUIState.Empty else SearchUIState.Data(shuffledList)
                        }
                    }
                }
            }
            return
        }

        viewModelScope.launch {
            recentlySearchedRepository.searchPokemonByNameAndFilterWithSort(
                searchText.value,
                searchOffset,
                selectedFilterOptionsList.value,
                selectedSortOption.value,
                ++lastSentRequest
            )
            recentlySearchedRepository.searchFlow.collect { newPokemonList ->
                if (newPokemonList.indexOfSearch == lastSentRequest || newPokemonList.indexOfSearch == -1) {
                    _pokemonList.update {
                        val shuffledList = newPokemonList.pokemons.shuffled()
                        if (shuffledList.isEmpty()) SearchUIState.Empty else SearchUIState.Data(shuffledList)
                    }
                }
            }
        }
    }

    override fun onPokemonClicked(pokemon: Pokemon, navController: NavController) {
        viewModelScope.launch {
            teamsRepository.addToTeam(pokemon, teamName)
            dismiss()
        }
    }

    override fun onLongClick(pokemon: Pokemon, navController: NavController) {
        viewModelScope.launch {
            recentlySearchedRepository.addToRecentlySearched(pokemon.name)
            navController.navigate(Screen.PokemonDetails.createRoute(pokemon.name))
        }
    }
}