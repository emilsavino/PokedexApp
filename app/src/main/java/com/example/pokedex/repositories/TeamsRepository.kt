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

    private val mutableTeamsFlow = MutableSharedFlow<List<Pokemon>>()
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

}