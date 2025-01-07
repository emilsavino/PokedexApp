package com.example.pokedex.mainViews.MyTeamsView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyTeamsViewModel: ViewModel() {
    private val teamsRepository = DependencyContainer.teamsRepository

    private val mutableTeamsState = MutableStateFlow<TeamsUIState>(TeamsUIState.Empty)
    val teamsState: MutableStateFlow<TeamsUIState> = mutableTeamsState

    init {
        viewModelScope.launch {
            teamsRepository.teamsFlow
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
        teamsRepository.fetchTeams()
    }
}

sealed class TeamsUIState {
    data class Data(val teams: List<Team>): TeamsUIState()
    object Loading: TeamsUIState()
    object Empty: TeamsUIState()
}