package com.example.pokedex.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.pokedex.data.PokemonDataStore
import com.example.pokedex.shared.Pokemon
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

    private val teams = mutableListOf<List<Pokemon>>()
    private val TEAMS_KEY = stringPreferencesKey("teams")
    private val gson = Gson()

    private val mutableTeamsFlow = MutableSharedFlow<List<List<Pokemon>>>()
    val teamsFlow: Flow<List<List<Pokemon>>> = mutableTeamsFlow.asSharedFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initializeCache()
        }
    }

    suspend fun initializeCache() {
        val savedPokemons = fetchTeamsFromDataStore()
        teams.clear()
        teams.addAll(savedPokemons)
    }

    suspend fun fetchSavedTeams() {
        mutableTeamsFlow.emit(teams)
    }

    suspend fun addTeam(newTeam: List<Pokemon>){
        teams.add(newTeam)
        updateDataStore()
    }

    suspend fun removeTeam(index: Int) {
        if (index in teams.indices){
            teams.removeAt(index)
            updateDataStore()
        }
    }

    suspend fun updateTeam(index: Int, updatedTeam: List<Pokemon>){
        if(index in teams.indices){
            teams[index] = updatedTeam
            updateDataStore()
        }
    }

    fun getTeam(index: Int): List<Pokemon>?{
        return if (index in teams.indices) teams[index] else null
    }

    private suspend fun updateDataStore() {
        val teamsJson = gson.toJson(teams)
        context.dataStore.edit { preferences ->
            preferences[TEAMS_KEY] = teamsJson
        }
    }

    private suspend fun fetchTeamsFromDataStore(): List<List<Pokemon>> {
        val preferences = context.dataStore.data.first()
        val teamsJson = preferences[TEAMS_KEY] ?: "[]"
        return gson.fromJson(teamsJson, Array<Array<Pokemon>>::class.java).map {
            it.toList()
        }
    }

}