package com.example.pokedex.data

import com.example.pokedex.shared.MockPokemon
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PokemonRepository {
    private val dataSource = MockPokemonDataStore()

    private val mutablePokemonFlow = MutableSharedFlow<Pokemon>()
    val pokemonFlow: Flow<Pokemon> = mutablePokemonFlow.asSharedFlow()

    private val mutablePokemonsFlow = MutableSharedFlow<List<MockPokemon>>()
    val pokemonsFlow: Flow<List<MockPokemon>> = mutablePokemonsFlow.asSharedFlow()

    private val mutableTeamsFlow = MutableSharedFlow<List<List<MockPokemon>>>()
    val teamsFlow: Flow<List<List<MockPokemon>>> = mutableTeamsFlow.asSharedFlow()

    private val mutableSavedPokemonsFlow = MutableSharedFlow<List<MockPokemon>>()
    val savedPokemonsFlow: Flow<List<MockPokemon>> = mutableSavedPokemonsFlow.asSharedFlow()

    suspend fun fetchPokemons() {
        mutablePokemonsFlow.emit(dataSource.fetchPokemons())
    }

    suspend fun fetchTeams() {
        mutableTeamsFlow.emit(dataSource.fetchTeams())
    }

    suspend fun fetchSaved() {
        mutableSavedPokemonsFlow.emit(dataSource.fetchSavedPokemons())
    }

    suspend fun searchPokemonByName(name: String) {
        mutablePokemonsFlow.emit(dataSource.searchPokemonByName(name))
    }

    fun getPokemonByName(name: String): MockPokemon {
        return dataSource.fetchPokemonByName(name)!!
    }
}