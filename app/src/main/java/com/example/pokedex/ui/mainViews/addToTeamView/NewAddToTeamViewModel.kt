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

class NewAddToTeamViewModel(private val teamName: String, private val dismiss: () -> Unit) : SearchViewModel() {
    private val teamsRepository = DependencyContainer.teamsRepository

    override fun searchPokemonList() {
        _pokemonList.update {
            SearchUIState.Loading
        }

        if (searchText.value.isEmpty() && selectedFilterOptionsList.value.isEmpty() && selectedSortOption.value.isEmpty())
        {
            SearchUIState.Empty
            return
        }

        viewModelScope.launch {
            recentlySearchedRepository.searchPokemonByNameAndFilterWithSort(searchText.value,searchOffset, selectedFilterOptionsList.value, selectedSortOption.value,++lastSentRequest)
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