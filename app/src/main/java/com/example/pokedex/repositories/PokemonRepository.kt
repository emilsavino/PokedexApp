package com.example.pokedex.repositories

import com.example.pokedex.data.PokemonDataStore
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonList
import com.example.pokedex.shared.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PokemonRepository {
    private var dataStore = PokemonDataStore()

    private val pokemonTeams = mutableListOf<List<Pokemon>>()

    private val mutablePokemonFlow = MutableSharedFlow<Pokemon>()
    val pokemonFlow: Flow<Pokemon> = mutablePokemonFlow.asSharedFlow()

    private var allPokemonsList = PokemonList(emptyList<Result>())

    private val mutablePokemonsFlow = MutableSharedFlow<PokemonList>()
    val pokemonsFlow: Flow<PokemonList> = mutablePokemonsFlow.asSharedFlow()

    private val mutableSearchFlow = MutableSharedFlow<List<Result>>()
    val searchFlow: Flow<List<Result>> = mutableSearchFlow.asSharedFlow()

    private val mutableTeamsFlow = MutableSharedFlow<List<List<Pokemon>>>()
    val teamsFlow: Flow<List<List<Pokemon>>> = mutableTeamsFlow.asSharedFlow()

    suspend fun fetchPokemons(limit: Int, offset: Int) {
        allPokemonsList = dataStore.fetchPokemons(limit,offset)
        mutablePokemonsFlow.emit(allPokemonsList)
    }

    suspend fun fetchTeams() {
        mutableTeamsFlow.emit(pokemonTeams)
    }

    suspend fun searchPokemonByName(name: String) {
        var filteredList = allPokemonsList.results.filter { it.name.contains(name,ignoreCase = true) }
        mutableSearchFlow.emit(filteredList)
    }

    suspend fun getPokemonByName(name: String) {
        mutablePokemonFlow.emit(dataStore.fetchPokemon(name))
    }
}