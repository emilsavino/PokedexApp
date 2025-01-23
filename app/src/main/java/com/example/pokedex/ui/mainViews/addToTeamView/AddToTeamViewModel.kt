package com.example.pokedex.ui.mainViews.addToTeamView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedex.dataClasses.Pokemon
import com.example.pokedex.mainViews.SearchView.SearchUIState
import com.example.pokedex.mainViews.SearchView.SearchViewModel
import com.example.pokedex.ui.navigation.Screen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddToTeamViewModel(private val teamName: String, private val dismiss: () -> Unit) : SearchViewModel() {
    override var suggestionText: String = "Suggested"

    override fun searchPokemonList() {
        _pokemonList.update {
            SearchUIState.Loading
        }

        if (searchText.value.isEmpty() && selectedFilterOptionsList.value.isEmpty() && selectedSortOption.value.isEmpty())
        {
            viewModelScope.launch {
                teamsRepository.fetchTeamSuggestions(teamName)
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
        }
    }

    override fun onPokemonClicked(pokemon: Pokemon, navController: NavController) {
        viewModelScope.launch {
            val message = teamsRepository.addToTeam(pokemon, teamName)
            if (message == "") {
                dismiss()
            } else {
                errorMessage = message
                showErrorAlert = true
            }
        }
    }

    override fun onLongClick(pokemon: Pokemon, navController: NavController) {
        viewModelScope.launch {
            recentlySearchedRepository.addToRecentlySearched(pokemon.name)
            navController.navigate(Screen.PokemonDetails.createRoute(pokemon.name))
        }
    }
}