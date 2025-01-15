package com.example.pokedex.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.pokedex.data.DatabaseService
import com.example.pokedex.shared.Pokemon
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "recently_viewed_preferences")

class RecentlyViewedRepository(private val context: Context) {
    private val databaseService = DatabaseService("recentlyViewed", Pokemon::class.java)
    private val recentlyViewedPokemons = mutableListOf<Pokemon>()
    private val RECENTLY_VIEWED_KEY = stringPreferencesKey("recently_viewed")
    private val gson = Gson()

    private val mutableRecentlyViewedPokemonsFlow = MutableSharedFlow<List<Pokemon>>()
    val recentlyViewedPokemonsFlow: Flow<List<Pokemon>> =
        mutableRecentlyViewedPokemonsFlow.asSharedFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initializeDatabase()
            if (recentlyViewedPokemons.isEmpty()) {
                initializeCache()
            }
            mutableRecentlyViewedPokemonsFlow.emit(recentlyViewedPokemons)
        }
    }

    private suspend fun initializeDatabase() {
        databaseService.addListenerForList { recentlyViewed ->
            if (recentlyViewed != null) {
                recentlyViewedPokemons.clear()
                recentlyViewedPokemons.addAll(recentlyViewed)
            }
        }
    }

    fun getCachedPokemon(name: String): Pokemon? {
        return recentlyViewedPokemons.find { it.name == name }
    }

    suspend fun initializeCache() {
        val savedPokemons = fetchRecentsFromDataStore()
        recentlyViewedPokemons.clear()
        recentlyViewedPokemons.addAll(savedPokemons)
    }

    suspend fun fetchRecents() {
        mutableRecentlyViewedPokemonsFlow.emit(recentlyViewedPokemons)
    }

    suspend fun addToRecents(pokemon: Pokemon) {
        if (recentlyViewedPokemons.contains(pokemon)) {
            recentlyViewedPokemons.remove(pokemon)
        }

        if (recentlyViewedPokemons.size >= 10) {
            recentlyViewedPokemons.removeAt(0)
        }

        recentlyViewedPokemons.add(pokemon)
        databaseService.storeList(recentlyViewedPokemons)
        updateDataStore()

    }

    private suspend fun updateDataStore() {
        val pokemonJson = gson.toJson(recentlyViewedPokemons)
        context.dataStore.edit { preferences ->
            preferences[RECENTLY_VIEWED_KEY] = pokemonJson
        }
    }

    private suspend fun fetchRecentsFromDataStore(): List<Pokemon> {
        val preferences = context.dataStore.data.first()
        val pokemonJson = preferences[RECENTLY_VIEWED_KEY] ?: "[]"
        return gson.fromJson(pokemonJson, Array<Pokemon>::class.java).toList()
    }
}