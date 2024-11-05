package com.example.pokedex.data

import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PokemonRepository {
    private val dataSource = MockPokemonDataStore()

    private val mutablePokemonsFlow = MutableSharedFlow<List<Pokemon>>()
    val fruitsFlow: Flow<List<Pokemon>> = mutablePokemonsFlow.asSharedFlow()

    suspend fun fetchPokemons() {
        mutablePokemonsFlow.emit(dataSource.fetchPokemons())
    }
}