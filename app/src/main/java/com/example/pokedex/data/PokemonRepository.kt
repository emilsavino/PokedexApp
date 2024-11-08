package com.example.pokedex.data

import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PokemonRepository {
    private val dataSource = MockPokemonDataStore()

    private val mutablePokemonsFlow = MutableSharedFlow<List<Pokemon>>()
    val pokemonsFlow: Flow<List<Pokemon>> = mutablePokemonsFlow.asSharedFlow()

    private val mutableTeamsFlow = MutableSharedFlow<List<List<Pokemon>>>()
    val teamsFlow: Flow<List<List<Pokemon>>> = mutableTeamsFlow.asSharedFlow()

    private val mutableSavedPokemonsFlow = MutableSharedFlow<List<Pokemon>>()
    val savedPokemonsFlow: Flow<List<Pokemon>> = mutableSavedPokemonsFlow.asSharedFlow()

    suspend fun fetchPokemons() {
        mutablePokemonsFlow.emit(dataSource.fetchPokemons())
    }

    suspend fun fetchTeams() {
        mutableTeamsFlow.emit(dataSource.fetchTeams())
    }

    suspend fun fetchSaved()
    {
        mutableSavedPokemonsFlow.emit(dataSource.fetchSavedPokemons())
    }
}