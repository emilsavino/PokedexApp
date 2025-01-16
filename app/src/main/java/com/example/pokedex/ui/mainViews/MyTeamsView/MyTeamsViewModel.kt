package com.example.pokedex.mainViews.MyTeamsView

import android.util.Log
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

    private val _teamsState = MutableStateFlow<TeamsUIState>(TeamsUIState.Loading)
    val teamsState: StateFlow<TeamsUIState> = _teamsState.asStateFlow()

    init {
        fetchTeams()
    }

    private fun fetchTeams() = viewModelScope.launch {
        _teamsState.update {
            TeamsUIState.Loading
        }

        teamsRepository.teamsFlow.collect { teams ->
            if (teams.isEmpty()) {
                _teamsState.update {
                    TeamsUIState.Empty
                }
            } else {
                Log.d("MyTeamsViewModel", "Teams Updated: ${teams.size} teams")
                _teamsState.update {
                    TeamsUIState.Data(teams)
                }
            }
        }
    }

    fun onDeleteTeamClicked(teamName: String) {
        isShowingDialog = true
        teamToEdit = teamName
    }

    fun deleteTeam(teamName: String) {
        viewModelScope.launch {
            teamsRepository.deleteTeam(teamName)
            fetchTeams()
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
        fetchTeams()
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