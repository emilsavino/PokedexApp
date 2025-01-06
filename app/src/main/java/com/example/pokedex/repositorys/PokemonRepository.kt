package com.example.pokedex.repositorys

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.pokedex.data.MockPokemonDataStore
import com.example.pokedex.data.PokemonDataStore
import com.example.pokedex.shared.Ability
import com.example.pokedex.shared.AbilityObject
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonList
import com.example.pokedex.shared.Result
import com.example.pokedex.shared.Sprites
import com.example.pokedex.shared.Type
import com.example.pokedex.shared.TypeObject
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
    private var dataStore = PokemonDataStore()

    private val pokemonTeams = mutableListOf<List<Pokemon>>()
    private val favouritePokemons = mutableListOf<Pokemon>()
    private val FAVOURITE_POKEMONS_KEY = stringPreferencesKey("favourite_pokemons")
    private val gson = Gson()

    private val mutablePokemonFlow = MutableSharedFlow<Pokemon>()
    val pokemonFlow: Flow<Pokemon> = mutablePokemonFlow.asSharedFlow()

    private var allPokemonsList = PokemonList(emptyList<Result>())

    private val mutablePokemonsFlow = MutableSharedFlow<PokemonList>()
    val pokemonsFlow: Flow<PokemonList> = mutablePokemonsFlow.asSharedFlow()

    private val mutableSearchFlow = MutableSharedFlow<List<Result>>()
    val searchFlow: Flow<List<Result>> = mutableSearchFlow.asSharedFlow()

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

    suspend fun fetchPokemons(limit: Int, offset: Int) {
        allPokemonsList = dataStore.fetchPokemons(limit,offset)
        mutablePokemonsFlow.emit(allPokemonsList)
    }

    suspend fun fetchTeams() {
        mutableTeamsFlow.emit(pokemonTeams)
    }

    suspend fun fetchSaved() {
        mutableSavedPokemonsFlow.emit(favouritePokemons)
    }

    suspend fun searchPokemonByName(name: String) {
        var filteredList = allPokemonsList.results.filter { it.name.contains(name,ignoreCase = true) }
        mutableSearchFlow.emit(filteredList)
    }

    suspend fun getPokemonByName(name: String) {
        mutablePokemonFlow.emit(dataStore.fetchPokemon(name))
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