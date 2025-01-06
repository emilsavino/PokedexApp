package com.example.pokedex.repositorys

import com.example.pokedex.data.MockPokemonDataStore
import com.example.pokedex.data.PokemonDataStore
import com.example.pokedex.shared.Ability
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.Sprites
import com.example.pokedex.shared.Type
import com.example.pokedex.shared.WhoIsThatPokemon
import kotlinx.coroutines.flow.MutableSharedFlow

class WhoIsThatPokemonRepository {
    val dataStore = PokemonDataStore()

    private val whoIsThatPokemonMutableSharedFlow = MutableSharedFlow<WhoIsThatPokemon>()
    val whoIsThatPokemonSharedFlow = whoIsThatPokemonMutableSharedFlow


    suspend fun getWhoIsThatPokemon()
    {
        val pokemon = Pokemon("", Sprites(""), emptyList<Ability>(), emptyList<Type>())
        val whoIsThatBro = WhoIsThatPokemon(pokemon, emptyList())
        whoIsThatPokemonSharedFlow.emit(whoIsThatBro)
    }
}