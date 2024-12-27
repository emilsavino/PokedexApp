package com.example.pokedex.data

import android.content.Context
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.WhoIsThatPokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object PokemonRepository {
    private lateinit var dataStore: MockPokemonDataStore

    private val mutablePokemonFlow = MutableSharedFlow<Pokemon>()
    val pokemonFlow: Flow<Pokemon> = mutablePokemonFlow.asSharedFlow()

    private val mutablePokemonsFlow = MutableSharedFlow<List<Pokemon>>()
    val pokemonsFlow: Flow<List<Pokemon>> = mutablePokemonsFlow.asSharedFlow()

    private val mutableTeamsFlow = MutableSharedFlow<List<List<Pokemon>>>()
    val teamsFlow: Flow<List<List<Pokemon>>> = mutableTeamsFlow.asSharedFlow()

    private val mutableSavedPokemonsFlow = MutableSharedFlow<List<Pokemon>>()
    val savedPokemonsFlow: Flow<List<Pokemon>> = mutableSavedPokemonsFlow.asSharedFlow()

    private val whoIsThatPokemonMutableSharedFlow = MutableSharedFlow<WhoIsThatPokemon>()
    val whoIsThatPokemonSharedFlow = whoIsThatPokemonMutableSharedFlow

    fun init(context: Context) {
        dataStore = MockPokemonDataStore(context)
    }

    suspend fun initializeCache() {
        dataStore.initializeCache()
    }

    suspend fun fetchPokemons() {
        mutablePokemonsFlow.emit(dataStore.fetchPokemons())
    }

    suspend fun fetchTeams() {
        mutableTeamsFlow.emit(dataStore.fetchTeams())
    }

    suspend fun fetchSaved() {
        mutableSavedPokemonsFlow.emit(dataStore.fetchSavedPokemons())
    }

    suspend fun searchPokemonByName(name: String) {
        mutablePokemonsFlow.emit(dataStore.searchPokemonByName(name))
    }

    fun getPokemonByName(name: String): Pokemon {
        return dataStore.fetchPokemonByName(name)!!
    }

    suspend fun savePokemon(pokemon: Pokemon) {
        dataStore.savePokemon(pokemon)
    }

    suspend fun removeFromFavorites(pokemon: Pokemon) {
        dataStore.removeFromFavourites(pokemon)
    }

    fun pokemonIsFavourite(pokemon: Pokemon): Boolean {
        return dataStore.pokemonIsFavourite(pokemon)
    }

    suspend fun getWhoIsThatPokemon()
    {
        whoIsThatPokemonSharedFlow.emit(dataStore.fetchWhoIsThatPokemon())
    }
}