package com.example.pokedex.repositories

import androidx.compose.ui.graphics.Color
import com.example.pokedex.dependencyContainer.DependencyContainer
import com.example.pokedex.shared.AbilityObject
import com.example.pokedex.shared.Option
import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.Sprites
import com.example.pokedex.shared.TypeObject
import com.example.pokedex.shared.WhoIsThatPokemon
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class WhoIsThatPokemonRepository {
    val dataStore = DependencyContainer.pokemonDataStore

    private val whoIsThatPokemonMutableSharedFlow = MutableSharedFlow<WhoIsThatPokemon>()
    val whoIsThatPokemonSharedFlow = whoIsThatPokemonMutableSharedFlow


    suspend fun getWhoIsThatPokemon()
    {
        val pokemon = dataStore.fetchPokemon("charmander")
        val options = listOf(
            Option("Charmander", Color.Green),
            Option("Squirtle", Color.Red),
            Option("Bulbasaur", Color.Red),
            Option("Pikachu", Color.Red)
        )
        val whoIsThatBro = WhoIsThatPokemon(pokemon, options)
        whoIsThatPokemonSharedFlow.emit(whoIsThatBro)
    }
}