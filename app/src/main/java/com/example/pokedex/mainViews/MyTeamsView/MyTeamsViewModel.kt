package com.example.pokedex.mainViews.MyTeamsView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyTeamsViewModel : ViewModel() {
    private val teamsRepository = DependencyContainer.teamsRepository

    private val _teamsState = MutableStateFlow<TeamsUIState>(TeamsUIState.Loading)
    val teamsState: StateFlow<TeamsUIState> = _teamsState

    init {
        fetchTeams()
    }

    private fun fetchTeams() {
        viewModelScope.launch {
            teamsRepository.teamsFlow.collect { teams ->
                if (teams.isEmpty()) {
                    _teamsState.value = TeamsUIState.Empty
                } else {
                    _teamsState.value = TeamsUIState.Data(teams)
                }
            }
        }
    }
}

sealed class TeamsUIState {
    object Loading : TeamsUIState()
    object Empty : TeamsUIState()
    data class Data(val teams: List<Team>) : TeamsUIState()
}