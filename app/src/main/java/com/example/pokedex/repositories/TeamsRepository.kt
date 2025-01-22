package com.example.pokedex.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import com.example.pokedex.data.DatabaseService
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.dataClasses.Pokemon
import com.example.pokedex.dataClasses.PokemonTypeResources
import com.example.pokedex.dataClasses.Team
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


private val Context.dataStore by preferencesDataStore(name = "team_preferences")

class TeamsRepository(private val context: Context) {
    private val recentlySearchedRepository = DependencyContainer.recentlySearchedRepository
    private val databaseService = DatabaseService("teams", Team::class.java)
    private val connectivityRepository = DependencyContainer.connectivityRepository
    private var hasInternet = connectivityRepository.isConnected.asLiveData()
    private var wasOffline = false

    private val pokemonTeams = mutableListOf<Team>()
    private val TEAMS_KEY = stringPreferencesKey("teams")
    private val gson = Gson()

    private val mutableTeamsFlow = MutableSharedFlow<List<Team>>()
    val teamsFlow: Flow<List<Team>> = mutableTeamsFlow.asSharedFlow()

    init {
        didSignIn()
    }

    fun didSignIn() {
        fetchSavedData()
        setObserver()
    }

    private fun fetchSavedData() {
        CoroutineScope(Dispatchers.IO).launch {
            initializeDatabase()
            if (pokemonTeams.isEmpty()) {
                initializeCache()
            }
            mutableTeamsFlow.emit(pokemonTeams)
        }
    }

    private fun setObserver() {
        hasInternet.observeForever { isConnected ->
            if (isConnected == true && wasOffline) {
                CoroutineScope(Dispatchers.IO).launch {
                    databaseService.storeList(pokemonTeams)
                }
                wasOffline = false
            } else if (isConnected == false) {
                wasOffline = true
            }
        }
    }

    private suspend fun initializeDatabase() {
        databaseService.addListenerForList { teams ->
            if (teams != null) {
                pokemonTeams.clear()
                pokemonTeams.addAll(teams)
            }
        }
    }

    suspend fun initializeCache() {
        val savedTeams = fetchTeamsFromDataStore()
        pokemonTeams.clear()
        pokemonTeams.addAll(savedTeams)
    }

    suspend fun fetchTeams() {
        mutableTeamsFlow.emit(pokemonTeams)
    }

    suspend fun addTeam(newTeam: Team): Boolean {
        for (team in pokemonTeams) {
            if (team.name == newTeam.name) {
                return false
            }
        }
        pokemonTeams.add(newTeam)
        databaseService.storeList(pokemonTeams)
        updateDataStore()
        return true
    }

    suspend fun deleteTeam(teamName: String) {
        pokemonTeams.removeAll { it.name == teamName }
        databaseService.storeList(pokemonTeams)
        updateDataStore()
        mutableTeamsFlow.emit(pokemonTeams)
    }

    suspend fun addToTeam(pokemon: Pokemon, teamName: String): String {
        val teamIndex = pokemonTeams.indexOfFirst { it.name == teamName }

        if (teamIndex == -1) {
            return "Team not found"
        }

        if (pokemonTeams[teamIndex].getPokemons().any { it.name == pokemon.name }) {
            return "Pokemon already in team"
        }

        val team = pokemonTeams[teamIndex]
        if (team.getPokemons().size >= 6) {
            return "Team is full"
        }
        team.addPokemon(pokemon)
        pokemonTeams[teamIndex] = team
        databaseService.storeList(pokemonTeams)
        updateDataStore()
        return ""
    }


    suspend fun deletePokemonFromTeam(name: String, teamName: String) {
        val teamIndex = pokemonTeams.indexOfFirst { it.name == teamName }

        val team = pokemonTeams[teamIndex]
        for (pokemon in team.getPokemons()) {
            if (pokemon.name == name) {
                team.removePokemon(pokemon)

                if (team.getPokemons().isEmpty()) {
                    deleteTeam(teamName)
                    databaseService.storeList(pokemonTeams)
                    updateDataStore()
                    return
                }
                pokemonTeams[teamIndex] = team
                databaseService.storeList(pokemonTeams)
                updateDataStore()
                break
            }
        }
        mutableTeamsFlow.emit(pokemonTeams)
    }

    fun getTeam(teamName: String): Team?{
        return pokemonTeams.firstOrNull { it.name == teamName }
    }

    private suspend fun updateDataStore() {
        val teamsJson = gson.toJson(pokemonTeams)
        context.dataStore.edit { preferences ->
            preferences[TEAMS_KEY] = teamsJson
        }
    }

    private suspend fun fetchTeamsFromDataStore(): List<Team> {
        val preferences = context.dataStore.data.first()
        val teamsJson = preferences[TEAMS_KEY] ?: "[]"
        return gson.fromJson(teamsJson, Array<Team>::class.java).toList()
    }

    suspend fun fetchTeamSuggestions(teamName: String) {
        val team = getTeam(teamName)

        val teamTypes = mutableListOf<String>()
        val allTypes = PokemonTypeResources().getAllTypes()
        val missingTypes = mutableListOf<String>()

        if (team != null) {
            for (pokemon in team.getPokemons()) {
                for (types in pokemon.types) {
                    teamTypes.add(types.type.name)
                }
            }
        }
        for (type in allTypes) {
            if (!teamTypes.contains(type)) {
                missingTypes.add(type)
            }
        }

        recentlySearchedRepository.searchShuffledPokemons(missingTypes)
    }
    suspend fun clearAllCache() {
        pokemonTeams.clear()
        updateDataStore()
        fetchTeams()
    }
}