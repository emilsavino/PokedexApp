package com.example.pokedex.mainViews.PokemonDetailView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PokemonDetailViewModel(private val name: String): ViewModel() {
    private val pokemonRepository = DependencyContainer.pokemonRepository
    private val favouritesRepository = DependencyContainer.favouritesRepository
    private val teamsRepository = DependencyContainer.teamsRepository
    private val recentlyViewedRepository = DependencyContainer.recentlyViewedRepository

    var favouriteButtonText by mutableStateOf("")
    var teamButtonText by mutableStateOf("Add to Team")
    var isFavorited by mutableStateOf(false)

    private val _pokemon: MutableStateFlow<PokemonDetailUIState> = MutableStateFlow(PokemonDetailUIState.Empty)
    val pokemon: StateFlow<PokemonDetailUIState> = _pokemon.asStateFlow()

    private val _teams: MutableStateFlow<List<Team>> = MutableStateFlow(emptyList())
    val teams: StateFlow<List<Team>> = teamsRepository.teamsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _selectedTeam = MutableStateFlow("")
    val selectedTeam: StateFlow<String> = _selectedTeam.asStateFlow()

    private val _newTeamName = MutableStateFlow("")
    val newTeamName: StateFlow<String> = _newTeamName.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showTeamCreationDialog = MutableStateFlow(false)
    val showTeamCreationDialog: StateFlow<Boolean> = _showTeamCreationDialog.asStateFlow()

    init {
        viewModelScope.launch {
            pokemonRepository.pokemonFlow.collect { newPokemon ->
                recentlyViewedRepository.addToRecents(newPokemon)
                _pokemon.update {
                    onFavouriteButton(newPokemon)
                    PokemonDetailUIState.Data(newPokemon)
                }
            }
        }

        viewModelScope.launch {
            teamsRepository.teamsFlow.collect { newTeams ->
                _teams.value = newTeams
            }
        }

        getPokemonByName()
    }

    fun setShowDialog(value: Boolean) {
        _showDialog.value = value
    }

    fun setSelectedTeam(teamName: String) {
        _selectedTeam.value = teamName
    }

    fun setNewTeamName(teamName: String) {
        _newTeamName.value = teamName
    }

    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    private fun getPokemonByName() = viewModelScope.launch {
        _pokemon.update {
            PokemonDetailUIState.Loading
        }
        pokemonRepository.getPokemonByName(name)
    }

    suspend fun savePokemon(pokemon: Pokemon) {
        if (favouritesRepository.pokemonIsFavourite(pokemon)) {
            favouritesRepository.removeFromFavourites(pokemon)
        } else {
            favouritesRepository.makeFavourite(pokemon)
            isFavorited = true
        }
        onFavouriteButton(pokemon)
    }

    private fun onFavouriteButton(pokemon: Pokemon) {
        isFavorited = favouritesRepository.pokemonIsFavourite(pokemon)
    }

    suspend fun addToTeam(pokemon: Pokemon, teamName: String) {
        teamsRepository.addToTeam(pokemon, teamName)
    }

    suspend fun confirmAddToTeam(pokemon: Pokemon) {
        if (_selectedTeam.value.isNotEmpty()) {
            try {
                addToTeam(pokemon, _selectedTeam.value)
                _errorMessage.value = null
            } catch (e: IllegalStateException) {
                _errorMessage.value = e.message
            }
        }
    }

    suspend fun createTeamWithPokemon(pokemon: Pokemon) {
        if (_newTeamName.value.isNotBlank()) {
            val newTeam = Team(name = _newTeamName.value, pokemons = listOf(pokemon))
            teamsRepository.addTeam(newTeam)
            _newTeamName.value = ""
        }
    }

    suspend fun createNewTeam(pokemon: Pokemon, teamName: String) {
        val newTeam = Team(name = teamName, pokemons = listOf(pokemon))
        teamsRepository.addTeam(newTeam)
    }

    fun setShowTeamCreationDialog(value: Boolean) {
        _showTeamCreationDialog.value = value
    }

    suspend fun onCreateTeam(pokemon: Pokemon): Boolean {
        if (_newTeamName.value.isNotBlank()) {
            val newTeam = Team(name = _newTeamName.value, pokemons = listOf(pokemon))
            teamsRepository.addTeam(newTeam)
            setNewTeamName("")
            setErrorMessage(null)
            return true
        } else {
            setErrorMessage("Team name cannot be empty")
            return false
        }
    }
}

sealed class PokemonDetailUIState {
    data class Data(val pokemon: Pokemon) : PokemonDetailUIState()
    object Loading : PokemonDetailUIState()
    object Empty : PokemonDetailUIState()
}