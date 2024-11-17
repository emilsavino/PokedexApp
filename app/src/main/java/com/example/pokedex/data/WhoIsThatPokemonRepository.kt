package com.example.pokedex.data

import com.example.pokedex.Screen
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.WhoIsThatPokemon
import kotlinx.coroutines.flow.MutableSharedFlow

class WhoIsThatPokemonRepository {
    val dataStore = MockPokemonDataStore()

    private val mutableSharedFlow = MutableSharedFlow<WhoIsThatPokemon>()
    val sharedFlow = mutableSharedFlow


    suspend fun getWhoIsThatPokemon()
    {
        sharedFlow.emit(dataStore.fetchWhoIsThatPokemon())
    }
}