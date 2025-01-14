package com.example.pokedex.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.Team
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


private val Context.dataStore by preferencesDataStore(name = "team_preferences")

class TeamsRepository(private val context: Context) {
    private val pokemonTeams = mutableListOf<Team>()
    private val TEAMS_KEY = stringPreferencesKey("teams")
    private val gson = Gson()

    private val mutableTeamsFlow = MutableStateFlow<List<Team>>(emptyList())
    val teamsFlow: StateFlow<List<Team>> = mutableTeamsFlow

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initializeCache()
        }
    }

    suspend fun initializeCache() {
        val savedTeams = fetchTeamsFromDataStore()
        pokemonTeams.clear()
        pokemonTeams.addAll(savedTeams)
        mutableTeamsFlow.emit(pokemonTeams)
    }

    suspend fun fetchTeams() {
        mutableTeamsFlow.emit(pokemonTeams)
    }

    suspend fun addTeam(newTeam: Team) : Boolean{
        for (team in pokemonTeams) {
            if (team.name == newTeam.name) {
                return false
            }
        }
        pokemonTeams.add(newTeam)
        updateDataStore()
        mutableTeamsFlow.emit(pokemonTeams)
        return true
    }

    suspend fun deleteTeam(teamName: String) {
        pokemonTeams.removeAll { it.name == teamName }
        updateDataStore()
        mutableTeamsFlow.emit(pokemonTeams)
    }

    suspend fun addToTeam(pokemon: Pokemon, teamName: String) : Boolean {
        val teamIndex = pokemonTeams.indexOfFirst { it.name == teamName }

        if (teamIndex == -1) {
            return false
        }

        val team = pokemonTeams[teamIndex]
        if (team.pokemons.size >= 6) {
            return false
        }

        val updatedTeam = team.copy(pokemons = team.pokemons + pokemon)
        pokemonTeams[teamIndex] = updatedTeam
        updateDataStore()
        mutableTeamsFlow.emit(pokemonTeams)
        return true
    }

    suspend fun deletePokemonFromTeam(name: String, teamName: String) {
        val teamIndex = pokemonTeams.indexOfFirst { it.name == teamName }

        val team = pokemonTeams[teamIndex]
        for (pokemon in team.pokemons) {
            if (pokemon.name == name) {
                val updatedTeam = team.copy(pokemons = team.pokemons - pokemon)
                if (updatedTeam.pokemons.isEmpty()) {
                    deleteTeam(teamName)
                    return
                }
                pokemonTeams[teamIndex] = updatedTeam
                updateDataStore()
                mutableTeamsFlow.emit(pokemonTeams)
                break
            }
        }
    }

    fun getTeam(index: Int): Team?{
        return if (index in pokemonTeams.indices) pokemonTeams[index] else null
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

}