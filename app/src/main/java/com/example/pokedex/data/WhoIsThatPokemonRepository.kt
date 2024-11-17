package com.example.pokedex.data

import androidx.compose.ui.graphics.Color
import com.example.pokedex.shared.WhoIsThatPokemon
import kotlinx.coroutines.flow.MutableSharedFlow

class WhoIsThatPokemonRepository {
    val dataStore = MockPokemonDataStore()

    private val whoIsThatPokemonMutableSharedFlow = MutableSharedFlow<WhoIsThatPokemon>()
    val whoIsThatPokemonSharedFlow = whoIsThatPokemonMutableSharedFlow


    suspend fun getWhoIsThatPokemon()
    {
        whoIsThatPokemonSharedFlow.emit(dataStore.fetchWhoIsThatPokemon())
    }
}