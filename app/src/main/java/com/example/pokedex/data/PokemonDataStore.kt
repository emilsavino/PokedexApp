package com.example.pokedex.data

import com.example.pokedex.shared.Pokemon
import com.example.pokedex.shared.PokemonList
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class PokemonDataStore {
    private val api = RetrofitInstance.apiService

    suspend fun fetchPokemons(): PokemonList {
        return withContext(Dispatchers.IO) {
            api.getPokemons(5,0)
        }
    }

    suspend fun fetchPokemon(name: String): Pokemon {
        val pokemon = api.getPokemon(name)
        pokemon.name = name
        return pokemon
    }
}