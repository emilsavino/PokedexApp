package com.example.pokedex.mainViews.PokemonDetailView

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.Team
import com.example.pokedex.shared.PokemonAttributes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PokemonDetailViewModel(private val name: String) : ViewModel() {
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
    private var currentPokemonName by mutableStateOf(name)

    private val _pokemon = MutableStateFlow<PokemonDetailUIState>(PokemonDetailUIState.Empty)
    val pokemon: StateFlow<PokemonDetailUIState> = _pokemon

    val teams: StateFlow<List<Team>> =
        teamsRepository.teamsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

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

        getPokemonByName()
    }

    private fun getPokemonByName() = viewModelScope.launch {
        _pokemon.update {
            PokemonDetailUIState.Loading
        }
        if (connectivityRepository.isConnected.asLiveData().value == false) {
            getCachedPokemon()
        } else {
            pokemonRepository.getPokemonDetailsByName(currentPokemonName)
        }
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
            val teamNameIsTaken = !teamsRepository.addTeam(Team(name = newTeamName, pokemons = listOf(pokemon)))
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

    private fun getCachedPokemon() {
        var pokemon = recentlyViewedRepository.getCachedPokemon(currentPokemonName)
        if (pokemon != null) {
            gotPokemonFromCach(pokemon)
            return
        }
        pokemon = teamsRepository.getCachedPokemon(currentPokemonName)
        if (pokemon != null) {
            gotPokemonFromCach(pokemon)
            return
        }
        pokemon = favouritesRepository.getCachedPokemon(currentPokemonName)
        if (pokemon != null) {
            gotPokemonFromCach(pokemon)
            return
        }
        _pokemon.update { PokemonDetailUIState.Empty }
    }

    private fun gotPokemonFromCach(pokemon: Pokemon) = viewModelScope.launch {
        _pokemon.update {
            PokemonDetailUIState.Data(pokemon.getOfflinePokemonAttributes())
        }
        recentlyViewedRepository.addToRecents(pokemon)
    }
}

sealed class PokemonDetailUIState {
    data class Data(val pokemon: PokemonAttributes): PokemonDetailUIState()
    object Loading: PokemonDetailUIState()
    object Empty: PokemonDetailUIState()
}