package com.example.pokedex.mainViews.MyTeamsView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.ui.navigation.Screen
import com.example.pokedex.dataClasses.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyTeamsViewModel : ViewModel() {
    private val teamsRepository = DependencyContainer.teamsRepository
    private val connectivityRepository = DependencyContainer.connectivityRepository
    var showNoInternetAlert by mutableStateOf(false)

    var isShowingDialog by mutableStateOf(false)
    var isShowingDeletePokemonDialog by mutableStateOf(false)
    var teamToEdit by mutableStateOf("")
    var pokemonToDelete by mutableStateOf("")

    private val _teamsState = MutableStateFlow<TeamsUIState>(TeamsUIState.Empty)
    val teamsState: StateFlow<TeamsUIState> = _teamsState.asStateFlow()

    init {
        viewModelScope.launch {
            teamsRepository.teamsFlow.collect { teams ->
                _teamsState.update {
                    if (teams.isEmpty()) {
                        TeamsUIState.Empty
                    } else {
                        TeamsUIState.Data(teams)
                    }
                }
            }
        }
        fetchTeams()
    }

    private fun fetchTeams() = viewModelScope.launch {
        _teamsState.update {
            TeamsUIState.Loading
        }
        teamsRepository.fetchTeams()
    }

    fun onDeleteTeamClicked(teamName: String) {
        isShowingDialog = true
        teamToEdit = teamName
    }

    fun deleteTeam(teamName: String) {
        viewModelScope.launch {
            teamsRepository.deleteTeam(teamName)
        }
        isShowingDialog = false
    }

    fun onLongClick(pokemonName: String, teamName: String) {
        isShowingDeletePokemonDialog = true
        teamToEdit = teamName
        pokemonToDelete = pokemonName
    }

    fun deletePokemonFromTeam() = viewModelScope.launch {
        teamsRepository.deletePokemonFromTeam(pokemonToDelete, teamToEdit)
        isShowingDeletePokemonDialog = false
    }

    fun onAddPokemonClicked(navController: NavController ,name: String) {
        if (connectivityRepository.isConnected.asLiveData().value == false) {
            showNoInternetAlert = true
            return
        }
        navController.navigate(Screen.AddToTeam.createRoute(name))
    }

}

sealed class TeamsUIState {
    object Loading : TeamsUIState()
    object Empty : TeamsUIState()
    data class Data(val teams: List<Team>) : TeamsUIState()
}