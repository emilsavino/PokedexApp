package com.example.pokedex.repositories

import com.example.pokedex.data.PokemonDataStore
import com.example.pokedex.shared.AbilityObject
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.Sprites
import com.example.pokedex.shared.TypeObject
import com.example.pokedex.shared.WhoIsThatPokemon
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class WhoIsThatPokemonRepository {
    val dataStore = PokemonDataStore()

    private val whoIsThatPokemonMutableSharedFlow = MutableSharedFlow<WhoIsThatPokemon>()
    val whoIsThatPokemonSharedFlow = whoIsThatPokemonMutableSharedFlow


    suspend fun getWhoIsThatPokemon()
    {
        val pokemon = Pokemon("", Sprites(""), emptyList<AbilityObject>(), emptyList<TypeObject>())
        val whoIsThatBro = WhoIsThatPokemon(pokemon, emptyList())
        whoIsThatPokemonSharedFlow.emit(whoIsThatBro)
    }
}