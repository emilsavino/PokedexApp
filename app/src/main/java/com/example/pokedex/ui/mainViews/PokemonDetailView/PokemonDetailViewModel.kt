package com.example.pokedex.mainViews.PokemonDetailView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.dataClasses.Pokemon
import com.example.pokedex.dataClasses.Team
import com.example.pokedex.dataClasses.PokemonAttributes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PokemonDetailViewModel() : ViewModel() {
    private val pokemonRepository = DependencyContainer.pokemonRepository
    private val favouritesRepository = DependencyContainer.favouritesRepository
    private val teamsRepository = DependencyContainer.teamsRepository
    private val recentlyViewedRepository = DependencyContainer.recentlyViewedRepository
    private val connectivityRepository = DependencyContainer.connectivityRepository

    var isFavorited by mutableStateOf(false)
    var showDialog by mutableStateOf(false)
    var selectedTeam by mutableStateOf("")
    var newTeamName by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
    var showTeamCreationDialog by mutableStateOf(false)
    private var previousPokemonsName = mutableListOf<String>()
    var currentPokemonName by mutableStateOf("")

    private val _pokemon = MutableStateFlow<PokemonDetailUIState>(PokemonDetailUIState.Empty)
    val pokemon: StateFlow<PokemonDetailUIState> = _pokemon

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    init {
        viewModelScope.launch {
            pokemonRepository.pokemonAttributesFlow.collect { newPokemon ->
                recentlyViewedRepository.addToRecents(newPokemon.pokemon)
                _pokemon.update {
                    onFavouriteButton(newPokemon.pokemon)
                    PokemonDetailUIState.Data(newPokemon)
                }
            }
        }

        viewModelScope.launch {
            teamsRepository.teamsFlow.collect { teams ->
                _teams.update {
                    teams
                }
            }
        }
        getTeams()
    }

    fun getPokemonByName() = viewModelScope.launch {
        _pokemon.update {
            PokemonDetailUIState.Loading
        }
        if (connectivityRepository.isConnected.asLiveData().value == false) {
            getCachedPokemon()
        } else {
            pokemonRepository.getPokemonDetailsByName(currentPokemonName)
        }
    }

    private fun getTeams() = viewModelScope.launch {
        teamsRepository.fetchTeams()
    }

    fun savePokemon(pokemon: Pokemon) = viewModelScope.launch {
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

    fun addToTeam(pokemon: Pokemon, teamName: String) = viewModelScope.launch {
        val success = teamsRepository.addToTeam(pokemon, teamName)
        if (success) {
            showDialog = false
            errorMessage = null
        } else {
            errorMessage = "Team is full"
        }
    }

    fun onCreateNewTeam() {
        showDialog = false
        showTeamCreationDialog = true
        errorMessage = null
    }

    fun onCreateTeamClicked(pokemon: Pokemon) = viewModelScope.launch {
        if (newTeamName.isNotBlank()) {
            val team = Team(newTeamName)
            team.addPokemon(pokemon)
            val teamNameIsTaken = !teamsRepository.addTeam(team)
            if (teamNameIsTaken) {
                errorMessage = "This name is already in use"
                return@launch
            }
            showTeamCreationDialog = false
            newTeamName = ""
            errorMessage = null
        } else {
            errorMessage = "Team name cannot be empty"
        }
    }

    fun onCancelTeamCreation() {
        showTeamCreationDialog = false
        newTeamName = ""
        errorMessage = null
    }

    fun onDismiss() {
        showDialog = false
        selectedTeam = ""
        errorMessage = null
    }

    fun navigateToEvo(nextName: String) {
        if (nextName == currentPokemonName)
        {
            return
        }
        previousPokemonsName.add(currentPokemonName)
        currentPokemonName = nextName
        getPokemonByName()
    }

    fun navigateToPrevious(navController: NavController) {
        if (previousPokemonsName.isNotEmpty()) {
            currentPokemonName = previousPokemonsName[previousPokemonsName.size - 1]
            previousPokemonsName.removeAt(previousPokemonsName.size - 1)
            getPokemonByName()
        } else {
            navController.popBackStack()
        }
    }

    private fun getCachedPokemon() = viewModelScope.launch {
        val dataStore = DependencyContainer.pokemonDataStore
        val pokemon = dataStore.getPokemonFromMapFallBackAPI(currentPokemonName)
        if (pokemon.name != "") {
            gotPokemonFromCache(pokemon)
        } else _pokemon.update { PokemonDetailUIState.Empty }
    }

    private fun gotPokemonFromCache(pokemon: Pokemon) = viewModelScope.launch {
        _pokemon.update {
            PokemonDetailUIState.Data(pokemon.getOfflinePokemonAttributes())
        }
        recentlyViewedRepository.addToRecents(pokemon)
    }

    fun getEmptyEvoText(): String {
        if (connectivityRepository.isConnected.asLiveData().value == false) {
            return "You need internet to see evolutions..."
        }

        return "This Pokémon evolves in mysterious ways, we have yet to discover its Evolution Chain!"
    }
}

sealed class PokemonDetailUIState {
    data class Data(val pokemon: PokemonAttributes): PokemonDetailUIState()
    object Loading: PokemonDetailUIState()
    object Empty: PokemonDetailUIState()
}