package com.example.pokedex.repositorys

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.pokedex.data.MockPokemonDataStore
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

class PokemonRepository(private val context: Context) {
    private var dataStore = MockPokemonDataStore()

    private val favouritePokemons = mutableListOf<Pokemon>()
    private val FAVOURITE_POKEMONS_KEY = stringPreferencesKey("favourite_pokemons")
    private val gson = Gson()

    private val mutablePokemonFlow = MutableSharedFlow<Pokemon>()
    val pokemonFlow: Flow<Pokemon> = mutablePokemonFlow.asSharedFlow()

    private val mutablePokemonsFlow = MutableSharedFlow<List<Pokemon>>()
    val pokemonsFlow: Flow<List<Pokemon>> = mutablePokemonsFlow.asSharedFlow()

    private val mutableTeamsFlow = MutableSharedFlow<List<List<Pokemon>>>()
    val teamsFlow: Flow<List<List<Pokemon>>> = mutableTeamsFlow.asSharedFlow()

    private val mutableSavedPokemonsFlow = MutableSharedFlow<List<Pokemon>>()
    val savedPokemonsFlow: Flow<List<Pokemon>> = mutableSavedPokemonsFlow.asSharedFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initializeCache()
        }
    }

    suspend fun initializeCache() {
        val savedPokemons = fetchSavedPokemonsFromDataStore()
        favouritePokemons.clear()
        favouritePokemons.addAll(savedPokemons)
    }

    suspend fun fetchPokemons() {
        mutablePokemonsFlow.emit(dataStore.fetchPokemons())
    }

    suspend fun fetchTeams() {
        mutableTeamsFlow.emit(dataStore.fetchTeams())
    }

    suspend fun fetchSaved() {
        mutableSavedPokemonsFlow.emit(favouritePokemons)
    }

    suspend fun searchPokemonByName(name: String) {
        mutablePokemonsFlow.emit(dataStore.searchPokemonByName(name))
    }

    fun getPokemonByName(name: String): Pokemon {
        return dataStore.fetchPokemonByName(name)!!
    }

    suspend fun savePokemon(pokemon: Pokemon) {
        if (!favouritePokemons.contains(pokemon)) {
            favouritePokemons.add(pokemon)
            updateDataStore()
        }
    }

    suspend fun removeFromFavourites(pokemon: Pokemon) {
        if (favouritePokemons.contains(pokemon)) {
            favouritePokemons.remove(pokemon)
            updateDataStore()
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
}