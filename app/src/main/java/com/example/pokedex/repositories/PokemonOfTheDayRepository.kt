package com.example.pokedex.repositories

import com.example.pokedex.data.PokemonDataStore
import com.example.pokedex.shared.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PokemonOfTheDayRepository {

    private val dataStore = PokemonDataStore()

    private val mutablePokemonOfTheDayFlow = MutableSharedFlow<Pokemon>()
    val pokemonOfTheDayFlow: Flow<Pokemon> = mutablePokemonOfTheDayFlow.asSharedFlow()


    suspend fun getPokemonOfTheDayByName(name: String) {
        mutablePokemonOfTheDayFlow.emit(dataStore.fetchPokemon(name))
    }
}