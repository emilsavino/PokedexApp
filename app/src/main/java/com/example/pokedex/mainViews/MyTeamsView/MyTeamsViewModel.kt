package com.example.pokedex.mainViews.MyTeamsView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyTeamsViewModel: ViewModel() {
    private val pokemonRepository = DependencyContainer.pokemonRepository

    private val mutableTeamsState = MutableStateFlow<TeamsUIState>(TeamsUIState.Empty)
    val teamsState: MutableStateFlow<TeamsUIState> = mutableTeamsState

    init {
        viewModelScope.launch {
            pokemonRepository.teamsFlow
                .collect { teams ->
                    mutableTeamsState.update {
                        TeamsUIState.Data(teams)
                    }
                }
        }
        fetchTeams()
    }


    private fun fetchTeams() = viewModelScope.launch {
        mutableTeamsState.update {
            TeamsUIState.Loading
        }
        pokemonRepository.fetchTeams()
    }
}

sealed class TeamsUIState {
    data class Data(val teams: List<List<Pokemon>>): TeamsUIState()
    object Loading: TeamsUIState()
    object Empty: TeamsUIState()
}