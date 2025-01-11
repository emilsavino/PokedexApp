package com.example.pokedex.mainViews.MyTeamsView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyTeamsViewModel : ViewModel() {
    private val teamsRepository = DependencyContainer.teamsRepository

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
                _teamsState.update {
                    TeamsUIState.Data(teams)
                }
            }
        }
    }

    fun deleteTeam(teamName: String) {
        viewModelScope.launch {
            teamsRepository.deleteTeam(teamName)
            fetchTeams()
        }
    }
}

sealed class TeamsUIState {
    object Loading : TeamsUIState()
    object Empty : TeamsUIState()
    data class Data(val teams: List<Team>) : TeamsUIState()
}