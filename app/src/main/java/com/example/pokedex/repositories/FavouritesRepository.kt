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

private val Context.dataStore by preferencesDataStore(name = "pokemon_preferences")

class FavouritesRepository(private val context: Context) {
    private val databaseService = DatabaseService<Pokemon>("saved", Pokemon::class.java)
    private val favouritePokemons = mutableListOf<Pokemon>()
    private val FAVOURITE_POKEMONS_KEY = stringPreferencesKey("favourite_pokemons")
    private val gson = Gson()

    private val mutableSavedPokemonsFlow = MutableSharedFlow<List<Pokemon>>()
    val savedPokemonsFlow: Flow<List<Pokemon>> = mutableSavedPokemonsFlow.asSharedFlow()

    init {
        if (!fetchFromDatabase()) {
            CoroutineScope(Dispatchers.IO).launch {
                initializeCache()
            }
        }
    }

    suspend fun initializeCache() {
        val savedPokemons = fetchSavedPokemonsFromDataStore()
        favouritePokemons.clear()
        favouritePokemons.addAll(savedPokemons)
        mutableSavedPokemonsFlow.emit(favouritePokemons)
    }

    suspend fun fetchSaved() {
        mutableSavedPokemonsFlow.emit(favouritePokemons)
    }

    suspend fun makeFavourite(pokemon: Pokemon) {
        if (!favouritePokemons.contains(pokemon)) {
            favouritePokemons.add(pokemon)
            updateDataStore()
            databaseService.storeArray(favouritePokemons)
        }
    }

    suspend fun removeFromFavourites(pokemon: Pokemon) {
        if (favouritePokemons.contains(pokemon)) {
            favouritePokemons.remove(pokemon)
            updateDataStore()
            databaseService.storeArray(favouritePokemons)
        }
    }

    fun pokemonIsFavourite(pokemon: Pokemon): Boolean {
        return favouritePokemons.contains(pokemon)
    }

    private suspend fun updateDataStore() {
        val pokemonJson = gson.toJson(favouritePokemons)
        context.dataStore.edit { preferences ->
            preferences[FAVOURITE_POKEMONS_KEY] = pokemonJson
        }
    }

    private suspend fun fetchSavedPokemonsFromDataStore(): List<Pokemon> {
        val preferences = context.dataStore.data.first()
        val pokemonJson = preferences[FAVOURITE_POKEMONS_KEY] ?: "[]"
        return gson.fromJson(pokemonJson, Array<Pokemon>::class.java).toList()
    }

    private fun fetchFromDatabase(): Boolean {
        var successful = false
        databaseService.addListenerForArray { pokemons ->
            if (pokemons.isNotEmpty()) {
                favouritePokemons.clear()
                favouritePokemons.addAll(pokemons)
                mutableSavedPokemonsFlow.tryEmit(favouritePokemons)
                CoroutineScope(Dispatchers.IO).launch {
                    updateDataStore()
                }
                successful = true

            }
        }
        return successful
    }
}
