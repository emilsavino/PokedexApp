package com.example.pokedex.mainViews.PokemonDetailView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.dependencyContainer.DependencyContainer.teamsRepository
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

class PokemonDetailViewModel(private val name: String) : ViewModel() {
    private val pokemonRepository = DependencyContainer.pokemonRepository
    private val favouritesRepository = DependencyContainer.favouritesRepository
    private val teamsRepository = DependencyContainer.teamsRepository
    private val recentlyViewedRepository = DependencyContainer.recentlyViewedRepository

    var favouriteButtonText by mutableStateOf("")
    var teamButtonText by mutableStateOf("Add to Team")
    var isFavorited by mutableStateOf(false)
    var showDialog by mutableStateOf(false)
    var selectedTeam by mutableStateOf("")
    var newTeamName by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
    var showTeamCreationDialog by mutableStateOf(false)

    private val _pokemon = MutableStateFlow<PokemonDetailUIState>(PokemonDetailUIState.Empty)
    val pokemon: StateFlow<PokemonDetailUIState> = _pokemon

    val teams: StateFlow<List<Team>> =
        teamsRepository.teamsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        viewModelScope.launch {
            pokemonRepository.pokemonFlow.collect { newPokemon ->
                recentlyViewedRepository.addToRecents(newPokemon)
                _pokemon.value = PokemonDetailUIState.Data(newPokemon)
                onFavouriteButton(newPokemon)
            }
        }

        getPokemonByName()
    }


    private fun getPokemonByName() = viewModelScope.launch {
        _pokemon.value = PokemonDetailUIState.Loading
        pokemonRepository.getPokemonByName(name)
    }

    suspend fun savePokemon(pokemon: Pokemon) {
        if (favouritesRepository.pokemonIsFavourite(pokemon)) {
            favouritesRepository.removeFromFavourites(pokemon)
        } else {
            favouritesRepository.makeFavourite(pokemon)
        }
        onFavouriteButton(pokemon)
    }

    private fun onFavouriteButton(pokemon: Pokemon) {
        isFavorited = favouritesRepository.pokemonIsFavourite(pokemon)
    }

    suspend fun addToTeam(pokemon: Pokemon, teamName: String) {
        try {
            teamsRepository.addToTeam(pokemon, teamName)
            errorMessage = null
        } catch (error: Exception) {
            errorMessage = error.message
        }
    }

    suspend fun confirmAddToTeam(pokemon: Pokemon) {
        if (selectedTeam.isNotEmpty()) {
            addToTeam(pokemon, selectedTeam)
        }
    }

suspend fun createTeamWithPokemon(pokemon: Pokemon) {
    if (newTeamName.isNotBlank()) {
        val newTeam = Team(name = newTeamName, pokemons = listOf(pokemon))
        teamsRepository.addTeam(newTeam)
        newTeamName = ""
    }
}

suspend fun createNewTeam(pokemon: Pokemon, teamName: String) {
    val newTeam = Team(name = teamName, pokemons = listOf(pokemon))
    teamsRepository.addTeam(newTeam)
}

    fun onCreateTeamClicked() {
        showTeamCreationDialog = true
    }

    fun onCancelTeamCreation() {
        showTeamCreationDialog = false
        newTeamName = ""
    }

    suspend fun createTeam(pokemon: Pokemon): Boolean {
        if (newTeamName.isNotBlank()) {
            val newTeam = Team(name = newTeamName, pokemons = listOf(pokemon))
            teamsRepository.addTeam(newTeam)
            showTeamCreationDialog = false
            newTeamName = ""
            errorMessage = null
            return true
        } else {
            errorMessage = "Team name cannot be empty"
            return false
        }
    }


    fun onTeamSelected(pokemon: Pokemon, teamName: String) {
        viewModelScope.launch {
            selectedTeam = teamName
            showDialog = false
            confirmAddToTeam(pokemon)
        }
    }

}

sealed class PokemonDetailUIState {
    data class Data(val pokemon: Pokemon) : PokemonDetailUIState()
    object Loading : PokemonDetailUIState()
    object Empty : PokemonDetailUIState()
}